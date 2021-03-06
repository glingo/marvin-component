package com.marvin.component.templating.parser;

import com.marvin.component.templating.node.BodyNode;
import com.marvin.component.templating.node.PrintNode;
import com.marvin.component.templating.node.RenderableNode;
import com.marvin.component.templating.node.RootNode;
import com.marvin.component.templating.node.TextNode;
import com.marvin.component.templating.node.expression.Expression;
import com.marvin.component.templating.node.operator.BinaryOperator;
import com.marvin.component.templating.node.operator.UnaryOperator;
import com.marvin.component.templating.token.Token;
import com.marvin.component.templating.token.TokenStream;
import com.marvin.component.templating.token.Type;
import com.marvin.component.templating.tokenParser.TokenParser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Parser implements ParserInterface {

    /**
     * Binary operators
     */
    private final Map<String, BinaryOperator> binaryOperators;

    /**
     * Unary operators
     */
    private final Map<String, UnaryOperator> unaryOperators;

    /**
     * Token parsers
     */
    private final Map<String, TokenParser> tokenParsers;

    /**
     * An expression parser.
     */
    private ExpressionParser expressionParser;

    /**
     * The TokenStream that we are converting into an Abstract Syntax Tree.
     */
    private TokenStream stream;

    /**
     * TokenParser objects provided by the extensions.
     */
    /**
     * used to keep track of the name of the block that we are currently inside
     * of. This is purely just for the parent() function.
     */
    private LinkedList<String> blockStack;

    /**
     * Constructor
     *
     * @param binaryOperators A map of binary operators
     * @param unaryOperators A map of unary operators
     * @param tokenParsers A map of token parsers
     */
    public Parser(Map<String, UnaryOperator> unaryOperators, Map<String, BinaryOperator> binaryOperators,
            Map<String, TokenParser> tokenParsers) {
        this.binaryOperators = binaryOperators;
        this.unaryOperators = unaryOperators;
        this.tokenParsers = tokenParsers;
    }

    @Override
    public RootNode parse(TokenStream stream) throws Exception {

        // expression parser
        this.expressionParser = new ExpressionParser(this, binaryOperators, unaryOperators);

        this.stream = stream;

        this.blockStack = new LinkedList<>();

        BodyNode body = subparse();

        RootNode root = new RootNode(body);
        return root;
    }

    @Override
    public BodyNode subparse() throws Exception {
        return subparse(null);
    }

    /**
     * The main method for the parser. This method does the work of converting a
     * TokenStream into a Node
     *
     * @param stopCondition A stopping condition provided by a token parser
     * @return Node The root node of the generated Abstract Syntax Tree
     * @throws java.lang.Exception
     */
    @Override
    public BodyNode subparse(StoppingCondition stopCondition) throws Exception {

        // these nodes will be the children of the root node
        List<RenderableNode> nodes = new ArrayList<>();

        Token token;
        while (!stream.isEOF()) {

            switch (stream.current().getType()) {
                case TEXT:

                    /*
                     * The current token is a text token. Not much to do here other
                     * than convert it to a text Node.
                     */
                    token = stream.current();
                    nodes.add(new TextNode(token.getValue(), token.getLineNumber()));
                    stream.next();
                    break;

                case PRINT_START:

                    /*
                     * We are entering a print delimited region at this point. These
                     * regions will contain some sort of expression so let's pass
                     * control to our expression parser.
                     */
                    // go to the next token because the current one is just the
                    // opening delimiter
                    token = stream.next();

                    Expression<?> expression = this.expressionParser.parseExpression();
                    nodes.add(new PrintNode(expression, token.getLineNumber()));

                    // we expect to see a print closing delimiter
                    stream.expect(Type.PRINT_END);

                    break;

                case EXECUTE_START:

                    // go to the next token because the current one is just the
                    // opening delimiter
                    stream.next();

                    token = stream.current();

                    /*
                     * We expect a name token at the beginning of every block.
                     *
                     * We do not use stream.expect() because it consumes the current
                     * token. The current token may be needed by a token parser
                     * which has provided a stopping condition. Ex. the 'if' token
                     * parser may need to check if the current token is either
                     * 'endif' or 'else' and act accordingly, thus we should not
                     * consume it.
                     */
                    if (!Type.NAME.equals(token.getType())) {
                        String msg = String.format("A block must start with a tag name at line %s in file %s.", token.getLineNumber(), stream.getFilename());
                        throw new Exception(msg);
                    }

                    // If this method was executed using a TokenParser and
                    // that parser provided a stopping condition (ex. checking
                    // for the 'endif' token) let's check for that condition
                    // now.
                    if (stopCondition != null && stopCondition.evaluate(token)) {
                        return new BodyNode(token.getLineNumber(), nodes);
                    }

                    // find an appropriate parser for this name
                    TokenParser tokenParser = tokenParsers.get(token.getValue());

                    if (tokenParser == null) {
                        String msg = String.format("Unexpected tag name \"%s\"", token.getValue(), token.getLineNumber(), stream.getFilename());
                        throw new Exception(msg);
                    }

                    RenderableNode node = tokenParser.parse(token, this);

                    // node might be null (ex. "extend" token parser)
                    if (node != null) {
                        nodes.add(node);
                    }

                    break;

                default:
                    String msg = String.format("Parser ended in undefined state.", stream.current().getLineNumber(), stream.getFilename());
                    throw new Exception(msg);
            }
        }

        // create the root node with the children that we have found
        return new BodyNode(stream.current().getLineNumber(), nodes);
    }

    @Override
    public TokenStream getStream() {
        return stream;
    }

    public void setStream(TokenStream stream) {
        this.stream = stream;
    }

    @Override
    public ExpressionParser getExpressionParser() {
        return this.expressionParser;
    }

    @Override
    public String peekBlockStack() {
        return blockStack.peek();
    }

    @Override
    public String popBlockStack() {
        return blockStack.pop();
    }

    @Override
    public void pushBlockStack(String blockName) {
        blockStack.push(blockName);
    }
}
