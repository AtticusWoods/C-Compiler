/*
 * Code formatter project
 * CS 4481
 */
package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolInfo;
import submit.SymbolTable;

/**
 *
 * @author edwajohn
 */
public class Assignment extends AbstractNode implements Expression {

  private final Mutable mutable;
  private final AssignmentType type;
  private final Expression rhs;

  public Assignment(Mutable mutable, String assign, Expression rhs) {
    this.mutable = mutable;
    this.type = AssignmentType.fromString(assign);
    this.rhs = rhs;
  }

  @Override
  public void toCminus(StringBuilder builder, final String prefix) {
    mutable.toCminus(builder, prefix);
    if (rhs != null) {
      builder.append(" ").append(type.toString()).append(" ");
      rhs.toCminus(builder, prefix);
    } else {
      builder.append(type.toString());
    }
  }

  @Override
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data,
                           SymbolTable symbolTable, RegisterAllocator regAllocator) {
    // Get variable offset
    int offset = symbolTable.getOffset(mutable.getId());

    code.append("# Get " + mutable.getId() + "'s offset from $sp from the symbol table and initialize "
                + mutable.getId() + "'s address with it. We'll add $sp later.\n");
    code.append("li $t0 ").append(offset).append("\n");
    code.append("# Add the stack pointer address to the offset.\n");
    code.append("add $t0 $t0 $sp\n");

    code.append("# Compute rhs for assignment =\n");

    // Handle different types of right-hand side expressions
    if (rhs instanceof NumConstant) {
        int value = ((NumConstant)rhs).getValue();
        code.append("li $t1 ").append(value).append("\n");
    } else {
        MIPSResult rhsResult = rhs.toMIPS(code, data, symbolTable, regAllocator);
        // If result is in a register, use it directly
        if (rhsResult.getRegister() != null) {
            code.append("move $t1 ").append(rhsResult.getRegister()).append("\n");
            regAllocator.clear(rhsResult.getRegister());
        }
    }

    code.append("# complete assignment statement with store\n");
    code.append("sw $t1 0($t0)\n");

    return MIPSResult.createVoidResult();
  }
}
