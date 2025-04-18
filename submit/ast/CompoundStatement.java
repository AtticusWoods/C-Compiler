/*
 * Code formatter project
 * CS 4481
 */
package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;
import submit.SymbolInfo;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

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
    
    // Add standard symbols
    symbolNames.add("println");
    symbolNames.add("return");
    
    // Extract variable declarations from statements
    for (Statement stmt : statements) {
      if (stmt instanceof VarDeclaration) {
        VarDeclaration varDecl = (VarDeclaration) stmt;
        for (String id : varDecl.getIds()) {
          symbolNames.add(id);
        }
      }
    }
    
    return symbolNames;
  }

  // Get the statements in this compound statement
  public List<Statement> getStatements() {
    return statements;
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
    // Use this scope's symbol table if available
    SymbolTable scopeTable = (this.symbolTable != null) ? this.symbolTable : symbolTable;
    
    // First pass: process all variable declarations to calculate stack space needed
    int totalVarSize = 0;
    for (Statement statement : statements) {
      if (statement instanceof VarDeclaration) {
        statement.toMIPS(code, data, scopeTable, regAllocator);
        totalVarSize += ((VarDeclaration) statement).getTotalSize();
      }
    }
    
    // Second pass: process all non-declaration statements
    for (Statement statement : statements) {
      if (!(statement instanceof VarDeclaration)) {
        statement.toMIPS(code, data, scopeTable, regAllocator);
      }
    }
    
    return MIPSResult.createVoidResult();
  }
}
