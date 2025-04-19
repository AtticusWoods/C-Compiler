# All program code is placed after the
# .text assembler directive
.text

# Declare main as a global function
.globl	main

j main

# Code for fib
fib:
# Entering a new scope
# Symbols on the symbol table
# println
# i
# return
# Update the stack pointer
addi $sp $sp -0
# Get i's offset from $sp from the symbol table and initialize
li $t0 -4
# Add the stack pointer address to the offset.
add $t0 $sp $t0
# Load the value of i.
lw $t0 0($t0)
li $t1 0
xor $t0 $t0 $t1
slti $t0 $t0 1
beq $t0 $zero label_0
li $t1 1
sw $t1 -8($sp)
jr $ra
j label_1
label_0:
label_1:
# Get i's offset from $sp from the symbol table and initialize
li $t0 -4
# Add the stack pointer address to the offset.
add $t0 $sp $t0
# Load the value of i.
lw $t0 0($t0)
li $t1 1
xor $t0 $t0 $t1
slti $t0 $t0 1
beq $t0 $zero label_2
li $t1 1
sw $t1 -8($sp)
jr $ra
j label_3
label_2:
label_3:
# Calling function fib
# Save $ra to a register
move $t0 $ra
# Save $t0-9 registers
sw $t0 -12($sp)
# Evaluate parameters and save to stack
# Get i's offset from $sp from the symbol table and initialize
li $t1 -4
# Add the stack pointer address to the offset.
add $t1 $sp $t1
# Load the value of i.
lw $t1 0($t1)
li $t2 1
sub $t1 $t1 $t2
sw $t1 -16($sp)
# Update the stack pointer
addi $sp $sp -12
# Call the function
jal fib
# Restore stack pointer
addi $sp $sp 12
# Restore $t0-9 registers
lw $t0 -12($sp)
# Restore $ra
move $ra $t0
# Get return value off the stack
lw $t0 -20($sp)
# Calling function fib
# Save $ra to a register
move $t1 $ra
# Save $t0-9 registers
sw $t0 -12($sp)
sw $t1 -16($sp)
# Evaluate parameters and save to stack
# Get i's offset from $sp from the symbol table and initialize
li $t2 -4
# Add the stack pointer address to the offset.
add $t2 $sp $t2
# Load the value of i.
lw $t2 0($t2)
li $t3 2
sub $t2 $t2 $t3
sw $t2 -20($sp)
# Update the stack pointer
addi $sp $sp -16
# Call the function
jal fib
# Restore stack pointer
addi $sp $sp 16
# Restore $t0-9 registers
lw $t0 -12($sp)
lw $t1 -16($sp)
# Restore $ra
move $ra $t1
# Get return value off the stack
lw $t1 -24($sp)
add $t0 $t0 $t1
sw $t0 -8($sp)
jr $ra
# Exiting scope. 
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
la $a0 label_4
li $v0 4
syscall
la $a0 newline
li $v0 4
syscall
# println
# Calling function fib
# Save $ra to a register
move $t0 $ra
# Save $t0-9 registers
sw $t0 -4($sp)
# Evaluate parameters and save to stack
li $t1 0
sw $t1 -8($sp)
# Update the stack pointer
addi $sp $sp -4
# Call the function
jal fib
# Restore stack pointer
addi $sp $sp 4
# Restore $t0-9 registers
lw $t0 -4($sp)
# Restore $ra
move $ra $t0
# Get return value off the stack
lw $t0 -12($sp)
move $a0 $t0
li $v0 1
syscall
la $a0 newline
li $v0 4
syscall
# println
# Calling function fib
# Save $ra to a register
move $t0 $ra
# Save $t0-9 registers
sw $t0 -4($sp)
# Evaluate parameters and save to stack
li $t1 1
sw $t1 -8($sp)
# Update the stack pointer
addi $sp $sp -4
# Call the function
jal fib
# Restore stack pointer
addi $sp $sp 4
# Restore $t0-9 registers
lw $t0 -4($sp)
# Restore $ra
move $ra $t0
# Get return value off the stack
lw $t0 -12($sp)
move $a0 $t0
li $v0 1
syscall
la $a0 newline
li $v0 4
syscall
# println
# Calling function fib
# Save $ra to a register
move $t0 $ra
# Save $t0-9 registers
sw $t0 -4($sp)
# Evaluate parameters and save to stack
li $t1 2
sw $t1 -8($sp)
# Update the stack pointer
addi $sp $sp -4
# Call the function
jal fib
# Restore stack pointer
addi $sp $sp 4
# Restore $t0-9 registers
lw $t0 -4($sp)
# Restore $ra
move $ra $t0
# Get return value off the stack
lw $t0 -12($sp)
move $a0 $t0
li $v0 1
syscall
la $a0 newline
li $v0 4
syscall
# println
# Calling function fib
# Save $ra to a register
move $t0 $ra
# Save $t0-9 registers
sw $t0 -4($sp)
# Evaluate parameters and save to stack
li $t1 3
sw $t1 -8($sp)
# Update the stack pointer
addi $sp $sp -4
# Call the function
jal fib
# Restore stack pointer
addi $sp $sp 4
# Restore $t0-9 registers
lw $t0 -4($sp)
# Restore $ra
move $ra $t0
# Get return value off the stack
lw $t0 -12($sp)
move $a0 $t0
li $v0 1
syscall
la $a0 newline
li $v0 4
syscall
# println
# Calling function fib
# Save $ra to a register
move $t0 $ra
# Save $t0-9 registers
sw $t0 -4($sp)
# Evaluate parameters and save to stack
li $t1 4
sw $t1 -8($sp)
# Update the stack pointer
addi $sp $sp -4
# Call the function
jal fib
# Restore stack pointer
addi $sp $sp 4
# Restore $t0-9 registers
lw $t0 -4($sp)
# Restore $ra
move $ra $t0
# Get return value off the stack
lw $t0 -12($sp)
move $a0 $t0
li $v0 1
syscall
la $a0 newline
li $v0 4
syscall
# println
# Calling function fib
# Save $ra to a register
move $t0 $ra
# Save $t0-9 registers
sw $t0 -4($sp)
# Evaluate parameters and save to stack
li $t1 5
sw $t1 -8($sp)
# Update the stack pointer
addi $sp $sp -4
# Call the function
jal fib
# Restore stack pointer
addi $sp $sp 4
# Restore $t0-9 registers
lw $t0 -4($sp)
# Restore $ra
move $ra $t0
# Get return value off the stack
lw $t0 -12($sp)
move $a0 $t0
li $v0 1
syscall
la $a0 newline
li $v0 4
syscall
# println
# Calling function fib
# Save $ra to a register
move $t0 $ra
# Save $t0-9 registers
sw $t0 -4($sp)
# Evaluate parameters and save to stack
li $t1 6
sw $t1 -8($sp)
# Update the stack pointer
addi $sp $sp -4
# Call the function
jal fib
# Restore stack pointer
addi $sp $sp 4
# Restore $t0-9 registers
lw $t0 -4($sp)
# Restore $ra
move $ra $t0
# Get return value off the stack
lw $t0 -12($sp)
move $a0 $t0
li $v0 1
syscall
la $a0 newline
li $v0 4
syscall
# println
# Calling function fib
# Save $ra to a register
move $t0 $ra
# Save $t0-9 registers
sw $t0 -4($sp)
# Evaluate parameters and save to stack
li $t1 7
sw $t1 -8($sp)
# Update the stack pointer
addi $sp $sp -4
# Call the function
jal fib
# Restore stack pointer
addi $sp $sp 4
# Restore $t0-9 registers
lw $t0 -4($sp)
# Restore $ra
move $ra $t0
# Get return value off the stack
lw $t0 -12($sp)
move $a0 $t0
li $v0 1
syscall
la $a0 newline
li $v0 4
syscall
# println
# Calling function fib
# Save $ra to a register
move $t0 $ra
# Save $t0-9 registers
sw $t0 -4($sp)
# Evaluate parameters and save to stack
li $t1 8
sw $t1 -8($sp)
# Update the stack pointer
addi $sp $sp -4
# Call the function
jal fib
# Restore stack pointer
addi $sp $sp 4
# Restore $t0-9 registers
lw $t0 -4($sp)
# Restore $ra
move $ra $t0
# Get return value off the stack
lw $t0 -12($sp)
move $a0 $t0
li $v0 1
syscall
la $a0 newline
li $v0 4
syscall
# println
# Calling function fib
# Save $ra to a register
move $t0 $ra
# Save $t0-9 registers
sw $t0 -4($sp)
# Evaluate parameters and save to stack
li $t1 9
sw $t1 -8($sp)
# Update the stack pointer
addi $sp $sp -4
# Call the function
jal fib
# Restore stack pointer
addi $sp $sp 4
# Restore $t0-9 registers
lw $t0 -4($sp)
# Restore $ra
move $ra $t0
# Get return value off the stack
lw $t0 -12($sp)
move $a0 $t0
li $v0 1
syscall
la $a0 newline
li $v0 4
syscall
# println
# Calling function fib
# Save $ra to a register
move $t0 $ra
# Save $t0-9 registers
sw $t0 -4($sp)
# Evaluate parameters and save to stack
li $t1 10
sw $t1 -8($sp)
# Update the stack pointer
addi $sp $sp -4
# Call the function
jal fib
# Restore stack pointer
addi $sp $sp 4
# Restore $t0-9 registers
lw $t0 -4($sp)
# Restore $ra
move $ra $t0
# Get return value off the stack
lw $t0 -12($sp)
move $a0 $t0
li $v0 1
syscall
la $a0 newline
li $v0 4
syscall
# Exiting scope. 
addi $sp $sp 0
li $v0 10
syscall

# All memory structures are placed after the
# .data assembler directive
.data

newline:	.asciiz "\n"
label_4:	.asciiz "This program prints the first 11 numbers of the Fibonacci sequence"
