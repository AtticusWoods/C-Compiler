/*
 * Code formatter project
 * CS 4481
 */
package submit.ast;

import java.util.List;
import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolInfo;
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
    // Create new scope
    this.symbolTable = parentSymbolTable;

    // Process declarations first (adds variables to symbol table)
    for (Statement s : statements) {
      if (s instanceof VarDeclaration) {
        s.toMIPS(code, data, symbolTable, regAllocator);
      }
    }


    // Print scope information AFTER declarations are processed
    code.append("# Entering a new scope.\n");
    code.append("# Symbols in symbol table:\n");
    for (String symbol : symbolTable.getSymbols()) {
      code.append("#  ").append(symbol).append("\n");
    }
    code.append("# Update the stack pointer.\n");
    code.append("addi $sp $sp -").append(symbolTable.getActivationRecordSize()).append("\n");

    // Process other statements
    for (Statement s : statements) {
      if (!(s instanceof VarDeclaration)) {
        s.toMIPS(code, data, symbolTable, regAllocator);
      }
    }

    // Clean up scope
    code.append("# Exiting scope.\n");
    code.append("addi $sp $sp ").append(symbolTable.getActivationRecordSize()).append("\n");

    return MIPSResult.createVoidResult();
  }
}
