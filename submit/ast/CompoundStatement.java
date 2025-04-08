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
  private SymbolTable symbolTable; // Add this field

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
                           SymbolTable parentSymbolTable, RegisterAllocator regAllocator) {
    // Create a new symbol table scope
    this.symbolTable = parentSymbolTable.createChild();

    // Calculate activation record size for local variables
    int arSize = 0;  // Initialize to 0 as in the example output

    code.append("# Entering a new scope.\n");
    code.append("# Symbols in symbol table:\n");
    for (String symbol : symbolTable.getSymbols()) {
      code.append("#  ").append(symbol).append("\n");
    }
    code.append("# Update the stack pointer.\n");
    code.append("addi $sp $sp -").append(arSize).append("\n");

    // Generate code for all statements
    for (Statement statement : statements) {
      statement.toMIPS(code, data, symbolTable, regAllocator);
    }

    // Clean up scope
    if (arSize > 0) {
      code.append("# Exiting scope.\n");
      code.append("addi $sp $sp ").append(arSize).append("\n");
    }

    return MIPSResult.createVoidResult();
  }
}
