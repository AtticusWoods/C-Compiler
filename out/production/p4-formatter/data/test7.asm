# All program code is placed after the
# .text assembler directive
.text

# Declare main as a global function
.globl	main

j main

# code for identity
identity:
# Entering a new scope.
# Symbols in symbol table:
#  println
#  x
#  return
# Update the stack pointer.
addi $sp $sp -0
# Exiting scope.
addi $sp $sp 0
jr $ra

# code for add
add:
# Entering a new scope.
# Symbols in symbol table:
#  println
#  x
#  y
#  return
# Update the stack pointer.
addi $sp $sp -0
# Exiting scope.
addi $sp $sp 0
jr $ra

# code for main
main:
# Entering a new scope.
# Symbols in symbol table:
#  println
#  return
# Update the stack pointer.
addi $sp $sp -0
# println
la $a0 datalabel0
li $v0 4
syscall
la $a0 newline
li $v0 4
syscall
# println
# Calling function identity
# Save $ra to a register
move $t0 $ra
# Save $t0-9 registers
sw $t0 -4($sp)
# Evaluate parameters and save to stack
# Update the stack pointer
add $sp $sp -4
# Call the function
jal identity
# Restore the stack pointer
add $sp $sp 4
# Restore $t0-9 registers
lw $t0 -4($sp)
# Restore $ra
move $ra $t0

# All memory structures are placed after the
# .data assembler directive
.data

newline:	.asciiz	"\n"
datalabel0:	.asciiz	"This program prints 7 7"
