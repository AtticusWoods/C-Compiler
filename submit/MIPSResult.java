/*
 */
package submit;

import submit.ast.VarType;

/**
 * This class represents the result of generating MIPS code. There are various
 * forms this result can take: 1) void, for cases where the calling node doesn't
 * need any information returned, such as a return statement. 2) register, for
 * cases where the called node needs to inform the calling node what register a
 * result is placed in, such as BinaryOperator. 3) address, for cases where the
 * returning result is in memory, such as StringConstant.
 *
 * To instantiate a MIPSResult object use the factory methods create...().
 *
 */
public class MIPSResult {

  private final String register;
  private final String address;
  private final VarType type;
  private final int intValue; // Added to store immediate integer values

  public static MIPSResult createVoidResult() {
    return new MIPSResult(null, null, null, 0);
  }

  public static MIPSResult createRegisterResult(String register, VarType type) {
    return new MIPSResult(register, null, type, 0);
  }

  public static MIPSResult createAddressResult(String address, VarType type) {
    return new MIPSResult(null, address, type, 0);
  }
  
  public static MIPSResult createIntResult(int value) {
    return new MIPSResult(null, null, VarType.INT, value);
  }

  public MIPSResult(String register, String address, VarType type, int intValue) {
    this.register = register;
    this.address = address;
    this.type = type;
    this.intValue = intValue;
  }
  
  // For backward compatibility
  public MIPSResult(String register, String address, VarType type) {
    this(register, address, type, 0);
  }

  /**
   * Anytime you get a register from a result you should seriously consider
   * calling regAllocator.clear(reg) after using the register to minimize
   * register usage.
   *
   * @return
   */
  public String getRegister() {
    return register;
  }

  public String getAddress() {
    return address;
  }

  public VarType getType() {
    return type;
  }
  
  public int getIntValue() {
    return intValue;
  }
}
