/*
 * Code formatter project
 * CS 4481
 */
package submit.ast;

import java.util.List;
import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;


/**
 *
 * @author edwajohn
 */
public class CompoundStatement extends AbstractNode implements Statement {

  private final List<Statement> statements;

  public CompoundStatement(List<Statement> statements) {
    this.statements = statements;
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    builder.append(prefix).append("{\n");
    for (Statement s : statements) {
      s.toCminus(builder, prefix + "  ");
    }
    builder.append(prefix).append("}\n");
  }

  @Override
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data,
                           SymbolTable symbolTable, RegisterAllocator regAllocator) {
    // Create a new symbol table scope
    SymbolTable childSymbolTable = symbolTable.createChild();

    // Generate code for all statements
    for (Statement statement : statements) {
      statement.toMIPS(code, data, childSymbolTable, regAllocator);
    }

    return MIPSResult.createVoidResult();
  }
}
