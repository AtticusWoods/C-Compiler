/*
 * Code formatter project
 * CS 4481
 */
package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
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
  public MIPSResult toMIPS(StringBuilder code,
                           StringBuilder data,
                           SymbolTable symbolTable,
                           RegisterAllocator regAllocator){
    if (id.equals("println")){
      List<MIPSResult> mipsResults = new ArrayList<>();
      code.append("# println\n");
      for (Expression arg: args) {
        mipsResults.add(arg.toMIPS(code, data, symbolTable, regAllocator));
      }
      MIPSResult arg = mipsResults.get(0);
      String moveOp = "move $a0 " + arg.getRegister() + "\n";
      String laOp = "la $a0 " + arg.getAddress() + "\n";
      code.append(arg.getAddress() == null ? moveOp : laOp);


      code.append("li $v0 ");
      if (arg.getType() == VarType.CHAR){
        code.append(4);
      } else {
        code.append(1);
      }
      code.append("\n");

      code.append("syscall\n");
      code.append("la $a0 newline\n");
      code.append("li $v0 4\n");
      code.append("syscall\n");
      if (arg.getRegister() != null){
        regAllocator.clear(arg.getRegister());
      }
    } else {
      code.append("# Calling function ").append(id).append("\n");
      code.append("# Save $ra to a register\n");
      String raReg = regAllocator.getT();
      if (raReg == null) {
        System.err.println("no regs in calll");
      }
      code.append("move ").append(raReg).append(" $ra\n");

      code.append("# Save $t0-9 registers\n");
      int regOffset = regAllocator.saveT(code, symbolTable.getActivationRecordSize());

      code.append("# Evaluate parameters and save to stack\n");
      int offset = -4;
      for (Expression arg : args) {
        MIPSResult argMips = arg.toMIPS(code, data, symbolTable, regAllocator);
        String argReg = argMips.getRegister();

        int val = offset - regOffset - symbolTable.getActivationRecordSize();
        code.append("sw ").append(argReg).append(" ").append(val).append("($sp)\n");
        regAllocator.clear(argReg);
        offset = offset - 4;
      }


      code.append("# Update the stack pointer\n");
      code.append("addi $sp $sp ").append(-regOffset - symbolTable.getActivationRecordSize()).append("\n");
      code.append("# Call the function\n");
      code.append("jal ").append(id).append("\n");

      code.append("# Restore stack pointer\n");
      code.append("addi $sp $sp ").append(regOffset + symbolTable.getActivationRecordSize()).append("\n");

      code.append("# Restore $t0-9 registers\n");
      regAllocator.restoreT(code, symbolTable.getActivationRecordSize());

      regAllocator.clear(raReg);
      code.append("# Restore $ra\n");
      code.append("move $ra ").append(raReg).append("\n");

      if (symbolTable.find(id).getType() != null) {
        code.append("# Get return value off the stack\n");
        int offsetOfReturn = -regOffset - symbolTable.getActivationRecordSize() + args.size() * -4 - 4;
        String returnReg = regAllocator.getT();
        code.append("lw ").append(returnReg).append(" ").append(offsetOfReturn).append("($sp)\n");
        return MIPSResult.createRegisterResult(returnReg, symbolTable.find(id).getType());
      }
    }
      return MIPSResult.createVoidResult();
  }


}
