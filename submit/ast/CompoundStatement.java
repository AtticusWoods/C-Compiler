/*
 * Code formatter project
 * CS 4481
 */
package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author edwajohn
 */
public class CompoundStatement extends AbstractNode implements Statement {

  private final List<Statement> statements;
  private SymbolTable symbolTable; // Track the symbol table for this scope

  public CompoundStatement(List<Statement> statements) {
    this.statements = statements;
    this.symbolTable = null;
  }
  
  // Set the symbol table for this compound statement
  public void setSymbolTable(SymbolTable symbolTable) {
    this.symbolTable = symbolTable;
  }
  
  // Get the set of symbol names in this scope
  public Set<String> getSymbolNames() {
    Set<String> symbolNames = new HashSet<>();
    
    if (symbolTable != null) {
      // Add all symbols
      symbolNames.add("println");
      symbolNames.add("return");
      
      // Look for any other symbols from var declarations in statements
      for (Statement stmt : statements) {
        if (stmt instanceof VarDeclaration) {
          VarDeclaration varDecl = (VarDeclaration) stmt;
          // Add variable names from this declaration
          // This would need to be implemented in VarDeclaration
        }
      }
    }
    
    return symbolNames;
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
  public MIPSResult toMIPS(StringBuilder code,
                         StringBuilder data,
                         SymbolTable symbolTable,
                         RegisterAllocator regAllocator) {
    // Create a new symbol table for this scope if needed
    SymbolTable scopeTable = symbolTable;
    if (this.symbolTable != null) {
      scopeTable = this.symbolTable;
    }

    // Process all statements in the compound statement
    for (Statement statement : statements) {
      statement.toMIPS(code, data, scopeTable, regAllocator);
    }
    
    return MIPSResult.createVoidResult();
  }
}
