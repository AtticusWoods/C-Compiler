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
      
      // Calculate stack offset based on local variables in the calling function
      int localVarsSize = symbolTable.getActivationRecordSize();
      
      // Stack offsets for saving registers and parameters
      int saveOffset = -4 - localVarsSize;     // Account for local variables
      int saveSize = 4 + localVarsSize;        // Account for local variables
      int paramOffset = saveOffset - 4;        // Start parameters after saved registers
      int usedTRegs = regAllocator.getUsedTRegCount(); // Number of t registers currently in use
      
      code.append("# Save $t0-9 registers\n");
      code.append("sw ").append(raReg).append(" ").append(saveOffset).append("($sp)\n");
      
      // Save additional t-registers if there are nested function calls
      for(int i = 0; i < usedTRegs; i++) {
          String reg = "$t" + i;
          if(!reg.equals(raReg)) { // Don't save the same register twice
              code.append("sw ").append(reg).append(" ").append(saveOffset - 4 * (i + 1)).append("($sp)\n");
          }
      }
      
      // Evaluate parameters and save to stack
      code.append("# Evaluate parameters and save to stack\n");
      
      // General case: handle any function with any number of arguments
      for (int i = 0; i < args.size(); i++) {
        Expression arg = args.get(i);
        
        // For nested function calls or complex expressions, evaluate them first
        if (arg instanceof Call || arg instanceof BinaryOperator) {
            MIPSResult argResult = arg.toMIPS(code, data, symbolTable, regAllocator);
            String tempReg;
            
            if (argResult.getRegister() != null) {
                tempReg = argResult.getRegister();
            } else {
                // If no register was returned but we have a value
                tempReg = regAllocator.getT();
                // Move value to temp register
                code.append("move ").append(tempReg).append(", $v0\n");
            }
            
            // Store parameter on stack
            code.append("sw ").append(tempReg).append(" ").append(paramOffset - (4 * i)).append("($sp)\n");
            regAllocator.clear(tempReg);
        }
        // Handle simple parameters (constants, variables)
        else if (arg instanceof NumConstant) {
            // Handle numeric constants directly
            NumConstant num = (NumConstant) arg;
            String tempReg = regAllocator.getT();
            code.append("li ").append(tempReg).append(" ").append(num.getValue()).append("\n");
            code.append("sw ").append(tempReg).append(" ").append(paramOffset - (4 * i)).append("($sp)\n");
            regAllocator.clear(tempReg);
        }
        // Handle variable parameters - need to load them from their memory locations
        else if (arg instanceof Mutable) {
            Mutable mutable = (Mutable) arg;
            SymbolInfo varInfo = symbolTable.find(mutable.getId());
            
            if (varInfo != null) {
                // Load variable from its memory location
                String addrReg = regAllocator.getT();
                String valueReg = regAllocator.getT();
                
                code.append("# Get ").append(mutable.getId()).append("'s offset from $sp from the symbol table and initialize ").append(mutable.getId()).append("'s address with it. We'll add $sp later.\n");
                code.append("li ").append(addrReg).append(" ").append(varInfo.getOffset()).append("\n");
                code.append("# Add the stack pointer address to the offset.\n");
                code.append("add ").append(addrReg).append(" ").append(addrReg).append(" $sp\n");
                code.append("# Load the value of ").append(mutable.getId()).append(".\n");
                code.append("lw ").append(valueReg).append(" 0(").append(addrReg).append(")\n");
                
                code.append("sw ").append(valueReg).append(" ").append(paramOffset - (4 * i)).append("($sp)\n");
//                regAllocator.clear(addrReg);
                regAllocator.clear(valueReg);
            }
        }
        else {
            // Any other type of expression as parameter
            MIPSResult paramResult = arg.toMIPS(code, data, symbolTable, regAllocator);
            if (paramResult.getRegister() != null) {
                code.append("sw ").append(paramResult.getRegister()).append(" ").append(paramOffset - (4 * i)).append("($sp)\n");
                regAllocator.clear(paramResult.getRegister());
            } else if (paramResult.getAddress() != null) {
                // Handle address-based parameters (like strings)
                String tempReg = regAllocator.getT();
                code.append("la ").append(tempReg).append(" ").append(paramResult.getAddress()).append("\n");
                code.append("sw ").append(tempReg).append(" ").append(paramOffset - (4 * i)).append("($sp)\n");
                regAllocator.clear(tempReg);
            }
        }
      }
      
      // Update stack pointer before call
      code.append("# Update the stack pointer\n");
      code.append("add $sp $sp -").append(4 + args.size() * 4).append("\n");
      
      // Make the call
      code.append("# Call the function\n");
      code.append("jal ").append(id).append("\n");
      
      // Restore stack pointer after call
      code.append("# Restore the stack pointer\n");
      code.append("add $sp $sp ").append(4 + args.size() * 4).append("\n");
      
      // Restore all saved registers
      code.append("# Restore $t0-9 registers\n");
      code.append("lw ").append(raReg).append(" ").append(saveOffset).append("($sp)\n");
      
      // Restore additional t-registers if they were saved
      for(int i = 0; i < usedTRegs; i++) {
          String reg = "$t" + i;
          if(!reg.equals(raReg)) { // Don't restore the same register twice
              code.append("lw ").append(reg).append(" ").append(saveOffset - 4 * (i + 1)).append("($sp)\n");
          }
      }
      
      // Restore $ra
      code.append("# Restore $ra\n");
      code.append("move $ra ").append(raReg).append("\n");
      regAllocator.clear(raReg);
      
      // Look up the function's return type
      SymbolInfo funcInfo = symbolTable.find(id);
      VarType returnType = (funcInfo != null) ? funcInfo.getType() : null;
      
      // Handle the return value if the function returns a value
      if (returnType != null && returnType != VarType.VOID) {
        // Calculate the offset for return value based on number of parameters
        int returnOffset = -8 - (4 * args.size());
        
        // Get a temporary register to hold the return value
        String returnReg = regAllocator.getT();
        code.append("# Get return value off stack\n");
        code.append("lw ").append(returnReg).append(" ").append(returnOffset).append("($sp)\n");
        
        // Return a register result with the correct return type
        return MIPSResult.createRegisterResult(returnReg, returnType);
      }
      
      return MIPSResult.createVoidResult();
    }
  }
}
