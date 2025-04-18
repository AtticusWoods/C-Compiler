# All program code is placed after the
# .text assembler directive
.text

# Declare main as a global function
.globl	main

j main
# code for main
main:
# Entering a new scope.
# Symbols in symbol table:
# Update the stack pointer.
addi $sp $sp -0
# println
la $a0 label_0
li $v0 4
syscall
la $a0 newline
li $v0 4
syscall
# println
add null null null

# All memory structures are placed after the
# .data assembler directive
.data

newline:	.asciiz	"\n"
label_0:	.asciiz	"This program prints the number 7"
