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

# Get a's offset from $sp from the symbol table and initialize
li $t0 -4
# Add the stack pointer address to the offset.
add $t0 $sp $t0
# Load the value of a.
lw $t0 0($t0)
li $t1 4
slt $t0 $t0 $t1
beq $t0 $zero label_1
# Entering a new scope
# Symbols on the symbol table
# a
# println
# return
# Update the stack pointer
addi $sp $sp -4
# println
la $a0 label_3
li $v0 4
syscall
la $a0 newline
li $v0 4
syscall

# exiting scope, restoring sp 
addi $sp $sp 4
j label_2
label_1:
label_2:
# Get a's offset from $sp from the symbol table and initialize
li $t0 -4
# Add the stack pointer address to the offset.
add $t0 $sp $t0
# Load the value of a.
lw $t0 0($t0)
li $t1 4
slt $t0 $t1 $t0
beq $t0 $zero label_4
# Entering a new scope
# Symbols on the symbol table
# a
# println
# return
# Update the stack pointer
addi $sp $sp -4
# println
la $a0 label_6
li $v0 4
syscall
la $a0 newline
li $v0 4
syscall

# exiting scope, restoring sp 
addi $sp $sp 4
j label_5
label_4:
label_5:
# Get a's offset from $sp from the symbol table and initialize
li $t0 -4
# Add the stack pointer address to the offset.
add $t0 $sp $t0
# Load the value of a.
lw $t0 0($t0)
li $t1 4
slt $t0 $t1 $t0
beq $t0 $zero label_7
# Entering a new scope
# Symbols on the symbol table
# a
# println
# return
# Update the stack pointer
addi $sp $sp -4
# println
la $a0 label_9
li $v0 4
syscall
la $a0 newline
li $v0 4
syscall

# exiting scope, restoring sp 
addi $sp $sp 4
j label_8
label_7:
# Entering a new scope
# Symbols on the symbol table
# a
# println
# return
# Update the stack pointer
addi $sp $sp -4
# println
la $a0 label_10
li $v0 4
syscall
la $a0 newline
li $v0 4
syscall

# exiting scope, restoring sp 
addi $sp $sp 4
label_8:
# Get a's offset from $sp from the symbol table and initialize
li $t0 -4
# Add the stack pointer address to the offset.
add $t0 $sp $t0
# Load the value of a.
lw $t0 0($t0)
li $t1 3
slt $t0 $t1 $t0
subi $t0 $t0 1
beq $t0 $zero label_11
# Entering a new scope
# Symbols on the symbol table
# a
# println
# return
# Update the stack pointer
addi $sp $sp -4
# println
la $a0 label_13
li $v0 4
syscall
la $a0 newline
li $v0 4
syscall

# exiting scope, restoring sp 
addi $sp $sp 4
j label_12
label_11:
label_12:
# Get a's offset from $sp from the symbol table and initialize
li $t0 -4
# Add the stack pointer address to the offset.
add $t0 $sp $t0
# Load the value of a.
lw $t0 0($t0)
li $t1 3
xor $t0 $t0 $t1
slti $t0 $t0 1
beq $t0 $zero label_14
# Entering a new scope
# Symbols on the symbol table
# a
# println
# return
# Update the stack pointer
addi $sp $sp -4
# println
la $a0 label_16
li $v0 4
syscall
la $a0 newline
li $v0 4
syscall

# exiting scope, restoring sp 
addi $sp $sp 4
j label_15
label_14:
label_15:
# Get a's offset from $sp from the symbol table and initialize
li $t0 -4
# Add the stack pointer address to the offset.
add $t0 $sp $t0
# Load the value of a.
lw $t0 0($t0)
li $t1 4
slt $t0 $t0 $t1
subi $t0 $t0 1
beq $t0 $zero label_17
# Entering a new scope
# Symbols on the symbol table
# a
# println
# return
# Update the stack pointer
addi $sp $sp -4
# println
la $a0 label_19
li $v0 4
syscall
la $a0 newline
li $v0 4
syscall

# exiting scope, restoring sp 
addi $sp $sp 4
j label_18
label_17:
# Entering a new scope
# Symbols on the symbol table
# a
# println
# return
# Update the stack pointer
addi $sp $sp -4
# println
la $a0 label_20
li $v0 4
syscall
la $a0 newline
li $v0 4
syscall

# exiting scope, restoring sp 
addi $sp $sp 4
label_18:
# exiting scope, restoring sp 
addi $sp $sp 0
li $v0 10
syscall
# All memory structures are placed after the
# .data assembler directive
.data

newline:	.asciiz "\n"
label_0:	.asciiz "This program prints [1..5] correct."
label_3:	.asciiz "1 correct"
label_6:	.asciiz "2 not correct"
label_9:	.asciiz "2 not correct"
label_10:	.asciiz "2 correct"
label_13:	.asciiz "3 correct"
label_16:	.asciiz "4 correct"
label_19:	.asciiz "5 not correct"
label_20:	.asciiz "5 correct"
