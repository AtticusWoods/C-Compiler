# All program code is placed after the
# .text assembler directive
.text

# Declare main as a global function
.globl	main

j main

# Code for identity
identity:
# Entering a new scope
# Symbols on the symbol table
# println
# x
# return
# Update the stack pointer
addi $sp $sp -0
# Get x's offset from $sp from the symbol table and initialize
li $t0 -4
# Add the stack pointer address to the offset.
add $t0 $sp $t0
# Load the value of x.
lw $t0 0($t0)
# store the return value on the stack
sw $t0 -8($sp)
jr $ra
# exiting scope, restoring sp 
addi $sp $sp 0
jr $ra

# Code for add
add:
# Entering a new scope
# Symbols on the symbol table
# println
# x
# y
# return
# Update the stack pointer
addi $sp $sp -0
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
# store the return value on the stack
sw $t0 -12($sp)
jr $ra
# exiting scope, restoring sp 
addi $sp $sp 0
jr $ra

# Code for main
main:
# Entering a new scope
# Symbols on the symbol table
# println
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
# function call identity
# store ra
move $t0 $ra
# store t registers
sw $t0 -4($sp)
# Evaluate args and place on the stack
li $t1 7
sw $t1 -8($sp)
# update stack pointer
addi $sp $sp -4
# call the function
jal identity
# restore stack pointer
addi $sp $sp 4
# restore t regs
lw $t0 -4($sp)
# restore ra
move $ra $t0
# get return value off the stack
lw $t0 -12($sp)
move $a0 $t0
li $v0 1
syscall
la $a0 newline
li $v0 4
syscall

# println
# function call add
# store ra
move $t0 $ra
# store t registers
sw $t0 -4($sp)
# Evaluate args and place on the stack
li $t1 3
sw $t1 -8($sp)
li $t1 4
sw $t1 -12($sp)
# update stack pointer
addi $sp $sp -4
# call the function
jal add
# restore stack pointer
addi $sp $sp 4
# restore t regs
lw $t0 -4($sp)
# restore ra
move $ra $t0
# get return value off the stack
lw $t0 -16($sp)
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
label_0:	.asciiz "This program prints 7 7"
