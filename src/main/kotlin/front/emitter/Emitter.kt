package front.emitter

import front.lexer.Token
import java.io.File

class Emitter(private val out: String = "out.c") {

    fun emit(stringList: List<String>) {
        fun recurse(stringList: List<String>, result: String = ""): String = when {
            stringList.isEmpty() -> result + printResult()
            else -> {
                val head = stringList.first()
                recurse(stringList.drop(1), result + when {
                    Token.Number.isNumber(head[0].toString()) -> push(head)
                    Token.Operator.isOperator(head) -> calculate(head)
                    else -> ""
                })
            }
        }

        val generated = programHead + programMain(recurse(stringList))

        File(out).printWriter().use {
            it.println(generated)
        }
    }

    private fun printResult() = "${popResult()} printf(\"%d\\n\", result);"

    private fun programMain(string: String) = "int main(void) { \n$string return 0;\n}"

    private fun push(number: String) = "push($number);"

    private fun calculate(operator: String) = listOf(popRight(), popLeft(), applyResult(operator)).joinToString("\n")

    private fun popLeft() = "left = pop();"

    private fun popRight() = "right = pop();"

    private fun popResult() = "result = pop();"

    private fun applyResult(operator: String) = push("left $operator right")

    private val programHead = "#include <stdio.h>\n" +
            "\n" +
            "int MAXSIZE = 32;\n" +
            "int stack[32];\n" +
            "int top = -1;\n" +
            "int left = 0;\n" +
            "int right = 0;\n" +
            "int result = 0;\n" +
            "\n" +
            "int isempty() {\n" +
            "\n" +
            "   if(top == -1)\n" +
            "      return 1;\n" +
            "   else\n" +
            "      return 0;\n" +
            "}\n" +
            "\n" +
            "int isfull() {\n" +
            "\n" +
            "   if(top == MAXSIZE)\n" +
            "      return 1;\n" +
            "   else\n" +
            "      return 0;\n" +
            "}\n" +
            "\n" +
            "int peek() {\n" +
            "   return stack[top];\n" +
            "}\n" +
            "\n" +
            "int pop() {\n" +
            "   int data;\n" +
            "\n" +
            "   if(!isempty()) {\n" +
            "      data = stack[top];\n" +
            "      top = top - 1;\n" +
            "      return data;\n" +
            "   } else {\n" +
            "      printf(\"Could not retrieve data, Stack is empty.\\n\");\n" +
            "   }\n" +
            "}\n" +
            "\n" +
            "int push(int data) {\n" +
            "\n" +
            "   if(!isfull()) {\n" +
            "      top = top + 1;\n" +
            "      stack[top] = data;\n" +
            "   } else {\n" +
            "      printf(\"Could not insert data, Stack is full.\\n\");\n" +
            "   }\n" +
            "}\n" +
            "\n"


}