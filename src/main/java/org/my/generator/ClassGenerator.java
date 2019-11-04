package org.my.generator;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassGenerator {

    /**
     * Compile and load java file
     * Only one class can be compiled and loaded
     *
     * @param javaScr java code, must noy contain nested/inner/anonymous classes
     * @return loaded class with new classLoader
     */
    public static Class<?> compileAndLoadClass(String javaScr) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        DiagnosticCollector<JavaFileObject> diagnostics =
                new DiagnosticCollector<>();

        String className = extractClassName(javaScr);

        final JavaByteObject byteObject = new JavaByteObject(className);

        StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(diagnostics, null, null);

        JavaFileManager fileManager = createFileManager(standardFileManager, byteObject);

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, getCompilationUnits(className, javaScr));

        if (!task.call()) {
            diagnostics.getDiagnostics().forEach(System.out::println);
        }
        try {
            fileManager.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        loading and using our compiled class
        final ClassLoader inMemoryClassLoader = createClassLoader(byteObject);
        try {
            return inMemoryClassLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    private static String extractClassName(String javaScr) {
        String packageName = "";
        String noCommentsScr = javaScr.replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)", "");
        Matcher packageMatcher = Pattern.compile("package\\s+(.*?);").matcher(noCommentsScr);
        if (packageMatcher.find()) {
            packageName = packageMatcher.group(1).trim() + ".";
        }
        Matcher classMatcher = Pattern.compile("class\\s+(.*?)[\\s{]").matcher(noCommentsScr);
        if (classMatcher.find()) {
            return packageName + classMatcher.group(1).trim();
        } else {
            throw new IllegalArgumentException("cannot find class in java source");
        }
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

    private static Iterable<? extends JavaFileObject> getCompilationUnits(String className, String src) {
        JavaStringObject stringObject = new JavaStringObject(className, src);
        return Arrays.asList(stringObject);
    }


    private static class JavaByteObject extends SimpleJavaFileObject {
        private ByteArrayOutputStream outputStream;

        protected JavaByteObject(String name) {
            super(URI.create("bytes:///" + name + name.replaceAll("\\.", "/")), Kind.CLASS);
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

    private static class JavaStringObject extends SimpleJavaFileObject {
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
