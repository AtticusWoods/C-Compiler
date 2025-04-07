//package main;
//
//import org.antlr.v4.runtime.CharStream;
//import org.antlr.v4.runtime.CharStreams;
//import org.antlr.v4.runtime.CommonTokenStream;
//import parser.CminusLexer;
//import parser.CminusParser;
//import submit.ASTVisitor;
//import org.antlr.v4.runtime.Parser;
//import submit.ast.Node;
//
//import java.io.IOException;
//import java.util.Properties;
//import java.util.logging.Handler;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//public class Main {
//    private static Logger LOGGER;
//
//    /**
//     * @param args the command line arguments
//     * @throws java.io.IOException
//     */
//    public static void main(String[] args) throws IOException {
//        // Logging setup
//        Level level = Level.INFO;
//
//        // TODO Enable trace-level code as needed. When true, LOGGER.fine() statements will be visible.
//        final boolean trace = true;
//        if (false) {
//            level = Level.ALL;
//        }
//
//        Properties props = System.getProperties();
//        props.setProperty("java.util.logging.SimpleFormatter.format", "%5$s%6$s%n");
//        Logger.getLogger("").setLevel(level);
//        for (Handler handler : Logger.getLogger("").getHandlers()) {
//            handler.setLevel(level);
//        }
//        LOGGER = Logger.getLogger(Parser.class.getName());
//
//        // TODO Update the filename as needed
//        final String filename = "data/test1.c";
//
//        LOGGER.info("");
//        LOGGER.info("Parsing " + filename + "\n");
//        LOGGER.info("");
//        final CharStream charStream = CharStreams.fromFileName(filename);
//        CminusLexer lexer = new CminusLexer(charStream);
//        CommonTokenStream tokens = new CommonTokenStream(lexer);
//        CminusParser parser = new CminusParser(tokens);
//        parser.setBuildParseTree(true);
//        CminusParser.ProgramContext programCtx = parser.program();
//
//        // TODO Implement building of the parse tree
//        LOGGER.info("");
//        LOGGER.info("Building abstract syntax tree");
//        LOGGER.info("");
//        ASTVisitor v = new ASTVisitor(LOGGER);
//        Node ast = v.visitProgram(programCtx);
//
//        // TODO Output formatted code
//        LOGGER.info("");
//        LOGGER.info("Formatted code:");
//        LOGGER.info("");
//        StringBuilder builder = new StringBuilder();
//        try {
//            ast.toCminus(builder, "");
//        } finally {
//            LOGGER.info(builder.toString());
//        }
//    }
//
//}
