package submit.ast;

import submit.RegisterAllocator;
import submit.SymbolTable;
import submit.MIPSResult;

public interface Node {
    void toCminus(StringBuilder builder, final String prefix);
    MIPSResult toMIPS(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator);
}
