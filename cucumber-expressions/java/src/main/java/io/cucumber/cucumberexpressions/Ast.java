package io.cucumber.cucumberexpressions;

import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

final class Ast {

    static final class AstNode {

        private final Type type;
        private final List<AstNode> nodes;
        private final String token;

        AstNode(Type type, String token) {
            this(type, null, token);
        }

        AstNode(Type type, AstNode... nodes) {
            this(type, asList(nodes));
        }

        AstNode(Type type, List<AstNode> nodes) {
            this(type, nodes, null);
        }

        private AstNode(Type type, List<AstNode> nodes, String token) {
            this.type = type;
            this.nodes = nodes;
            this.token = token;
        }

        enum Type {
            TEXT_NODE,
            OPTIONAL_NODE,
            ALTERNATION_NODE,
            ALTERNATIVE_NODE,
            PARAMETER_NODE,
            EXPRESSION_NODE
        }

        List<AstNode> getNodes() {
            return nodes;
        }

        Type getType() {
            return type;
        }

        String getText() {
            if(token != null)
                return token;

            return getNodes().stream()
                    .map(AstNode::getText)
                    .collect(joining());
        }

        @Override
        public String toString() {
            return toString(0).toString();
        }

        private StringBuilder toString(int depth){
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < depth; i++) {
                sb.append("\t");
            }
            sb.append("AstNode{" + "type=").append(type);

            if (token != null) {
                sb.append(", token=").append(token);
            }

            if (nodes != null) {
                sb.append("\n");
                for (AstNode node : nodes) {
                    sb.append(node.toString(depth + 1));
                    sb.append("\n");
                }
                for (int i = 0; i < depth; i++) {
                    sb.append("\t");
                }
            }

            sb.append('}');
            return sb;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AstNode astNode = (AstNode) o;
            return type == astNode.type &&
                    Objects.equals(nodes, astNode.nodes) &&
                    Objects.equals(token, astNode.token);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, nodes, token);
        }
    }


    static final class Token {

        final String text;
        final Token.Type type;

        Token(String text, Token.Type type) {
            this.text = text;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Token token = (Token) o;
            return text.equals(token.text) &&
                    type == token.type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(text, type);
        }

        @Override
        public String toString() {
            return "Token{" +
                    "text='" + text + '\'' +
                    ", type=" + type +
                    '}';
        }

        enum Type {
            START_OF_LINE,
            END_OF_LINE,
            WHITE_SPACE,
            BEGIN_OPTIONAL('(', "optional text"),
            END_OPTIONAL(')', "optional text"),
            BEGIN_PARAMETER('{', "a parameter"),
            END_PARAMETER('}', "a parameter"),
            ALTERNATION('/', "alternation"),
            TEXT;

            private final int token;
            private final String purpose;

            Type(){ this(-1, null);}

            Type(int token, String purpose) {
                this.token = token;
                this.purpose = purpose;
            }

            public String getPurpose() {
                return purpose;
            }

            int codePoint() {
                if (token == -1) {
                    throw new IllegalStateException(name() + " does not have a code point");
                }

                return token;
            }
        }
    }
}
