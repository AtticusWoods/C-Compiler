# All program code is placed after the
# .text assembler directive
.text

# Declare main as a global function
.globl	main

j main

# Code for add
add:
# Entering a new scope
# Symbols on the symbol table
# println
# x
# y
# i
# return
# Update the stack pointer
addi $sp $sp -0
# println
# Get x's offset from $sp from the symbol table and initialize
li $t0 -4
# Add the stack pointer address to the offset.
add $t0 $sp $t0
# Load the value of x.
lw $t0 0($t0)
# Get y's offset from $sp from the symbol table and initialize
li $t1 -8
# Add the stack pointer address to the offset.
add $t1 $sp $t1
# Load the value of y.
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
jr $ra

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

# function call add
# store ra
move $t0 $ra
# store t registers
sw $t0 -12($sp)
# Evaluate args and place on the stack
li $t1 3
sw $t1 -16($sp)
li $t1 4
sw $t1 -20($sp)
# update stack pointer
addi $sp $sp -12
# call the function
jal add
# restore stack pointer
addi $sp $sp 12
# restore t regs
lw $t0 -12($sp)
# restore ra
move $ra $t0
# function call add
# store ra
move $t0 $ra
# store t registers
sw $t0 -12($sp)
# Evaluate args and place on the stack
# Get a's offset from $sp from the symbol table and initialize
li $t1 -4
# Add the stack pointer address to the offset.
add $t1 $sp $t1
# Load the value of a.
lw $t1 0($t1)
sw $t1 -16($sp)
# Get b's offset from $sp from the symbol table and initialize
li $t1 -8
# Add the stack pointer address to the offset.
add $t1 $sp $t1
# Load the value of b.
lw $t1 0($t1)
sw $t1 -20($sp)
# update stack pointer
addi $sp $sp -12
# call the function
jal add
# restore stack pointer
addi $sp $sp 12
# restore t regs
lw $t0 -12($sp)
# restore ra
move $ra $t0
# exiting scope, restoring sp 
addi $sp $sp 0
li $v0 10
syscall
# All memory structures are placed after the
# .data assembler directive
.data

newline:	.asciiz "\n"
label_0:	.asciiz "This program prints 7 7"
