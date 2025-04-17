/*
 * Code formatter project
 * CS 4481
 */
package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolInfo;
import submit.SymbolTable;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author edwajohn
 */
public class Call extends AbstractNode implements Expression {

  private final String id;
  private final List<Expression> args;

  public Call(String id, List<Expression> args) {
    this.id = id;
    this.args = new ArrayList<>(args);
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    builder.append(id).append("(");
    for (Expression arg : args) {
      arg.toCminus(builder, prefix);
      builder.append(", ");
    }
    if (!args.isEmpty()) {
      builder.setLength(builder.length() - 2);
    }
    builder.append(")");
  }

  @Override
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data,
                           SymbolTable symbolTable, RegisterAllocator regAllocator) {
    if (id.equals("println")) {
      // Handle println specially
      code.append("# println\n");

      if (args.size() != 1) {
        throw new RuntimeException("println takes exactly 1 argument");
      }

      // Get the argument to print
      Expression argExpr = args.get(0);
      
      // Handle all argument types
      MIPSResult argResult = argExpr.toMIPS(code, data, symbolTable, regAllocator);

      // Handle string case - use la directly
      if (argResult.getAddress() != null) {
        code.append("la $a0 ").append(argResult.getAddress()).append("\n");
        code.append("li $v0 4\n"); // String print syscall
      }
      // Handle integer case
      else if (argResult.getRegister() != null) {
        code.append("move $a0 ").append(argResult.getRegister()).append("\n");
        regAllocator.clear(argResult.getRegister());
        code.append("li $v0 1\n"); // Integer print syscall
      } else {
        // If $v0 contains the return value from a function call
        code.append("move $a0, $v0\n");
        code.append("li $v0, 1\n"); // Integer print syscall
      }

      code.append("syscall\n");

      // Print newline
      code.append("la $a0 newline\n");
      code.append("li $v0 4\n");
      code.append("syscall\n");

      return MIPSResult.createVoidResult();
    } else {
      // For regular function calls
      code.append("# Calling function ").append(id).append("\n");
      
      // Save $ra to a register
      code.append("# Save $ra to a register\n");
      String raReg = regAllocator.getT();
      code.append("move ").append(raReg).append(" $ra\n");
      
      // Calculate frame sizes and offsets
      int frameBaseSize = 4; // For $ra storage
      int paramCount = args.size();
      int paramSize = paramCount * 4;
      int usedTRegs = regAllocator.getUsedTRegCount();
      int tRegSaveSize = usedTRegs * 4;
      
      // Offsets from $sp
      int raOffset = -4;
      int tRegSaveOffset = raOffset - tRegSaveSize;
      int paramOffset = tRegSaveOffset - paramSize;
      
      // Save return address
      code.append("# Save $t0-9 registers\n");
      code.append("sw ").append(raReg).append(" ").append(raOffset).append("($sp)\n");
      
      // Save all in-use t-registers
      int currentOffset = raOffset;
      for(int i = 0; i < 10; i++) {
          String reg = "$t" + i;
          if(regAllocator.isInUse(reg) && !reg.equals(raReg)) {
              currentOffset -= 4;
              code.append("sw ").append(reg).append(" ").append(currentOffset).append("($sp)\n");
          }
      }

      // Evaluate parameters and save to stack
      code.append("# Evaluate parameters and save to stack\n");
      
      // Process each argument
      for (int i = 0; i < args.size(); i++) {
          Expression arg = args.get(i);
          int currentParamOffset = paramOffset - (i * 4);
          
          // For nested function calls as arguments
          if (arg instanceof Call) {
              MIPSResult argResult = arg.toMIPS(code, data, symbolTable, regAllocator);
              String tempReg = regAllocator.getT();
              // For function calls, get return value from $v0
              code.append("move ").append(tempReg).append(", $v0\n");
              code.append("sw ").append(tempReg).append(" ").append(currentParamOffset).append("($sp)\n");
              regAllocator.clear(tempReg);
          } 
          // For numeric constants
          else if (arg instanceof NumConstant) {
              NumConstant num = (NumConstant) arg;
              String tempReg = regAllocator.getT();
              code.append("li ").append(tempReg).append(" ").append(num.getValue()).append("\n");
              code.append("sw ").append(tempReg).append(" ").append(currentParamOffset).append("($sp)\n");
              regAllocator.clear(tempReg);
          } 
          // For variable parameters (load from memory location)
          else if (arg instanceof Mutable) {
              Mutable mutable = (Mutable) arg;
              SymbolInfo varInfo = symbolTable.find(mutable.getId());
              
              if (varInfo != null) {
                  String addrReg = regAllocator.getT();
                  String valueReg = regAllocator.getT();
                  
                  code.append("# Get ").append(mutable.getId()).append("'s offset from $sp from the symbol table and initialize ").append(mutable.getId()).append("'s address with it. We'll add $sp later.\n");
                  code.append("li ").append(addrReg).append(" ").append(varInfo.getOffset()).append("\n");
                  code.append("# Add the stack pointer address to the offset.\n");
                  code.append("add ").append(addrReg).append(" ").append(addrReg).append(" $sp\n");
                  code.append("# Load the value of ").append(mutable.getId()).append(".\n");
                  code.append("lw ").append(valueReg).append(" 0(").append(addrReg).append(")\n");
                  
                  code.append("sw ").append(valueReg).append(" ").append(currentParamOffset).append("($sp)\n");
                  regAllocator.clear(addrReg);
                  regAllocator.clear(valueReg);
              }
          } 
          // For other expression types
          else {
              MIPSResult paramResult = arg.toMIPS(code, data, symbolTable, regAllocator);
              if (paramResult.getRegister() != null) {
                  code.append("sw ").append(paramResult.getRegister()).append(" ").append(currentParamOffset).append("($sp)\n");
                  regAllocator.clear(paramResult.getRegister());
              } else if (paramResult.getAddress() != null) {
                  String tempReg = regAllocator.getT();
                  code.append("la ").append(tempReg).append(" ").append(paramResult.getAddress()).append("\n");
                  code.append("sw ").append(tempReg).append(" ").append(currentParamOffset).append("($sp)\n");
                  regAllocator.clear(tempReg);
              }
          }
      }
      
      // Calculate total stack frame size
      int frameSize = frameBaseSize + tRegSaveSize + paramSize;
      
      // Update stack pointer before call
      code.append("# Update the stack pointer\n");
      code.append("add $sp $sp -").append(frameSize).append("\n");
      
      // Make the function call
      code.append("# Call the function\n");
      code.append("jal ").append(id).append("\n");
      
      // Restore stack pointer
      code.append("# Restore the stack pointer\n");
      code.append("add $sp $sp ").append(frameSize).append("\n");
      
      // Restore saved registers (in reverse order)
      code.append("# Restore $t0-9 registers\n");
      
      // First restore other t-registers
      currentOffset = raOffset;
      for(int i = 0; i < 10; i++) {
          String reg = "$t" + i;
          if(regAllocator.isInUse(reg) && !reg.equals(raReg)) {
              currentOffset -= 4;
              code.append("lw ").append(reg).append(" ").append(currentOffset).append("($sp)\n");
          }
      }
      
      // Then restore the RA register
      code.append("lw ").append(raReg).append(" ").append(raOffset).append("($sp)\n");
      
      // Restore return address
      code.append("# Restore $ra\n");
      code.append("move $ra ").append(raReg).append("\n");
      regAllocator.clear(raReg);
      
      // Look up function information in the root symbol table
      // This ensures we can find global functions from any scope
      SymbolInfo funcInfo = null;
      SymbolTable currentTable = symbolTable;
      
      // First go all the way up to the root symbol table
      while (currentTable.getParent() != null) {
          currentTable = currentTable.getParent();
      }
      
      // Now look for the function in the root table
      funcInfo = currentTable.find(id);
      
//      // Debug output to help diagnose issues
//      code.append("# Looking for function: ").append(id).append("\n");
//      if (funcInfo == null) {
//          code.append("# WARNING: Function ").append(id).append(" not found in symbol table\n");
//      } else {
//          code.append("# Found function ").append(id).append(" with return type ").append(funcInfo.getType()).append("\n");
//      }
      
      VarType returnType = (funcInfo != null) ? funcInfo.getType() : null;
      
      // Handle the return value for non-void functions
      if (returnType != null && returnType != VarType.VOID) {
          // Calculate offset for return value
          int returnOffset = -8 - (paramCount * 4);
          
          // Get temp register for return value
          String returnReg = regAllocator.getT();
          code.append("# Get return value off stack\n");
          code.append("lw ").append(returnReg).append(" ").append(returnOffset).append("($sp)\n");
          
          // Return a register with the correct type
          return MIPSResult.createRegisterResult(returnReg, returnType);
      }
      
      return MIPSResult.createVoidResult();
    }
  }
}
