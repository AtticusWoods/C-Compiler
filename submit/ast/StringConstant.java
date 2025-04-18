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

  private final String value;

  public StringConstant(String value) {
    // Remove quotes that ANTLR parser includes with string constants
    this.value = value.substring(1, value.length() - 1);
  }

  public void toCminus(StringBuilder builder, final String prefix) {
    builder.append("\"").append(value).append("\"");
  }

  @Override
  public MIPSResult toMIPS(StringBuilder code,
                           StringBuilder data,
                           SymbolTable symbolTable,
                           RegisterAllocator regAllocator) {
    // generate a unique label
    String lbl = symbolTable.getUniqueLabel();
    // emit into .data
    data.append(lbl)
            .append(":\t.asciiz\t\"")
            .append(value)   // value no longer includes the quotes
            .append("\"\n");
    // tell the caller where it is
    return MIPSResult.createAddressResult(lbl, VarType.CHAR);
  }
}
