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
# Exiting scope. 
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
# Calling function add
# Save $ra to a register
move $t0 $ra
# Save $t0-9 registers
sw $t0 -12($sp)
# Evaluate parameters and save to stack
li $t1 3
sw $t1 -16($sp)
li $t1 4
sw $t1 -20($sp)
# Update the stack pointer
addi $sp $sp -12
# Call the function
jal add
# Restore stack pointer
addi $sp $sp 12
# Restore $t0-9 registers
lw $t0 -12($sp)
# Restore $ra
move $ra $t0
# Get a' offset from $sp from the symbol table and initialize x's address with it. We'll add $sp later.
li $t0-4
# Add the stack pointer address to the offset.
add $t0 $sp $t0
# Compute rhs for assignment
li $t1 5
# Complete assignment statement with store
sw $t1 0($t0)
# Get b' offset from $sp from the symbol table and initialize x's address with it. We'll add $sp later.
li $t0-8
# Add the stack pointer address to the offset.
add $t0 $sp $t0
# Compute rhs for assignment
li $t1 2
# Complete assignment statement with store
sw $t1 0($t0)
# Calling function add
# Save $ra to a register
move $t0 $ra
# Save $t0-9 registers
sw $t0 -12($sp)
# Evaluate parameters and save to stack
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
# Update the stack pointer
addi $sp $sp -12
# Call the function
jal add
# Restore stack pointer
addi $sp $sp 12
# Restore $t0-9 registers
lw $t0 -12($sp)
# Restore $ra
move $ra $t0
# Exiting scope. 
addi $sp $sp 0
li $v0 10
syscall

# All memory structures are placed after the
# .data assembler directive
.data

newline:	.asciiz "\n"
label_0:	.asciiz "This program prints 7 7"
