# All program code is placed after the
# .text assembler directive
.text

# Declare main as a global function
.globl	main

j main

# Code for main
main:
# Entering a new scope
# Symbols on the symbol table
# a
# println
# b
# return
# Update the stack pointer
addi $sp $sp -0
# println
la $a0 label_0
li $v0 4
syscall
la $a0 newline
li $v0 4
syscall

# println
# Get a's offset from $sp from the symbol table and initialize
li $t0 -4
# Add the stack pointer address to the offset.
add $t0 $sp $t0
# Load the value of a.
lw $t0 0($t0)
# Get b's offset from $sp from the symbol table and initialize
li $t1 -8
# Add the stack pointer address to the offset.
add $t1 $sp $t1
# Load the value of b.
lw $t1 0($t1)
add $t0 $t0 $t1
move $a0 $t0
li $v0 1
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
label_0:	.asciiz "This program prints the number 7"
