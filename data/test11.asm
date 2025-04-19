# All program code is placed after the
# .text assembler directive
.text

# Declare main as a global function
.globl	main

j main
#code for main
main:
# Entering a new scope
# Symbols on the symbol table
# println
# i
# return
# Update the stack pointer
addi $sp $sp -0
# get i offset from the stack pointer.
li $t0 -4
# load offset + sp to get the address of i
add $t0 $sp $t0
# compute rhs for assignment
li $t1 0
# complete assignment by storing rhs in address
sw $t1 0($t0)

# println
la $a0 datalabel0
li $v0 4
syscall
la $a0 newline
li $v0 4
syscall

# exiting scope, restoring sp 
addi $sp $sp 0
li $v0 10
syscall
# All memory structures are placed after the
# .data assembler directive
.data

newline:	.asciiz "\n"
datalabel0:	.asciiz "This program prints 0 through 9."
