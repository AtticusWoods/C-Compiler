/*
 * Code formatter project
 * CS 4481
 */
package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;

/**
 *
 * @author edwajohn
 */
public class StringConstant extends AbstractNode implements Expression {
  private static boolean newlineAdded = false;

  private final String value;

  public StringConstant(String value) {
    this.value = value;
  }

  public void toCminus(StringBuilder builder, final String prefix) {
    builder.append("\"").append(value).append("\"");
  }

  @Override
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data,
                           SymbolTable symbolTable, RegisterAllocator regAllocator) {
    // Add newline constant if not already added
    if (!newlineAdded) {
      data.append("newline:\t.asciiz\t\"\\n\"\n");
      newlineAdded = true;
    }

    // Create a unique label for this string
    String label = "data" + symbolTable.getUniqueLabel().replace("_", "");
    data.append(label).append(":\t.asciiz\t").append(value).append("\n");

    return MIPSResult.createAddressResult(label, VarType.CHAR);
  }
}
