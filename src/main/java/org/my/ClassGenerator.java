package org.my;

import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassGenerator {

    public static void main(String[] args) throws Exception {
        Writer w = new StringWriter();
        JavaGenerator.generate(
                new InputStreamReader(new FileInputStream("/Users/vminin/IdeaProjects/jFlex-reloadable/src/main/resources/example.jflex")),
                w);
        System.out.println(w);

        m(null);
    }


    public static void m(String[] args) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        DiagnosticCollector<JavaFileObject> diagnostics =
                new DiagnosticCollector<>();

        String className = "Test";

        final JavaByteObject byteObject = new JavaByteObject(className);

        StandardJavaFileManager standardFileManager =
                compiler.getStandardFileManager(diagnostics, null, null);

        JavaFileManager fileManager = createFileManager(standardFileManager,
                byteObject);

        JavaCompiler.CompilationTask task = compiler.getTask(null,
                fileManager, diagnostics, null, null, getCompilationUnits(className));

        if (!task.call()) {
            diagnostics.getDiagnostics().forEach(System.out::println);
        }
        fileManager.close();

        //loading and using our compiled class
        final ClassLoader inMemoryClassLoader = createClassLoader(byteObject);
        Class<?> test = (Class<?>) inMemoryClassLoader.loadClass(className);
        System.out.println(test.newInstance());

    }

    private static JavaFileManager createFileManager(StandardJavaFileManager fileManager,
                                                     JavaByteObject byteObject) {
        return new ForwardingJavaFileManager<StandardJavaFileManager>(fileManager) {
            @Override
            public JavaFileObject getJavaFileForOutput(Location location,
                                                       String className, JavaFileObject.Kind kind,
                                                       FileObject sibling) throws IOException {
                return byteObject;
            }
        };
    }

    private static ClassLoader createClassLoader(final JavaByteObject byteObject) {
        return new ClassLoader() {
            @Override
            public Class<?> findClass(String name) throws ClassNotFoundException {
                //no need to search class path, we already have byte code.
                byte[] bytes = byteObject.getBytes();
                return defineClass(name, bytes, 0, bytes.length);
            }
        };
    }

    public static Iterable<? extends JavaFileObject> getCompilationUnits(String className) {
        JavaStringObject stringObject =
                new JavaStringObject(className, getSource());
        return Arrays.asList(stringObject);
    }

    public static String getSource() {
        return "public class Test {" +
                "public void doSomething(){" +
                "System.out.println(\"testing\");}}";
    }

    public static class JavaByteObject extends SimpleJavaFileObject {
        private ByteArrayOutputStream outputStream;

        protected JavaByteObject(String name) throws URISyntaxException {
            super(URI.create("bytes:///"+name + name.replaceAll("\\.", "/")), Kind.CLASS);
            outputStream = new ByteArrayOutputStream();
        }

        //overriding this to provide our OutputStream to which the
        // bytecode can be written.
        @Override
        public OutputStream openOutputStream() {
            return outputStream;
        }

        public byte[] getBytes() {
            return outputStream.toByteArray();
        }
    }

    public static class JavaStringObject extends SimpleJavaFileObject {
        private final String source;

        protected JavaStringObject(String name, String source) {
            super(URI.create("string:///" + name.replaceAll("\\.", "/") +
                    Kind.SOURCE.extension), Kind.SOURCE);
            this.source = source;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return source;
        }
    }


}
