# All program code is placed after the
# .text assembler directive
.text

# Declare main as a global function
.globl	main

j main
#code for sum
sum:
# Entering a new scope
# Symbols on the symbol table
# println
# x
# i
# sum
# n
# return
# Update the stack pointer
addi $sp $sp -0
# get i offset from the stack pointer.
li $t0 -16
# load offset + sp to get the address of i
add $t0 $sp $t0
# compute rhs for assignment
li $t1 0
# complete assignment by storing rhs in address
sw $t1 0($t0)

# get sum offset from the stack pointer.
li $t0 -20
# load offset + sp to get the address of sum
add $t0 $sp $t0
# compute rhs for assignment
li $t1 0
# complete assignment by storing rhs in address
sw $t1 0($t0)

# get sum offset from the stack pointer.
li $t0 -20
# load offset + sp to get the address of sum
add $t0 $sp $t0
# load the value of sum
lw $t0 0($t0)
# store the return value on the stack
sw $t0 -12($sp)
jr $ra
# exiting scope, restoring sp 
addi $sp $sp 0
jr $ra
#code for main
main:
# Entering a new scope
# Symbols on the symbol table
# a
# println
# i
# return
# Update the stack pointer
addi $sp $sp -0
# println
la $a0 datalabel0
li $v0 4
syscall
la $a0 newline
li $v0 4
syscall

# get i offset from the stack pointer.
li $t0 -8
# load offset + sp to get the address of i
add $t0 $sp $t0
# compute rhs for assignment
li $t1 0
# complete assignment by storing rhs in address
sw $t1 0($t0)

# println
# calling function sum
# store ra
move $t0 $ra
# store t registers
sw $t0 -12($sp)
# Evaluate args and place on the stack
# get a offset from the stack pointer.
li $t1 -4
# load offset + sp to get the address of a
add $t1 $sp $t1
# load the value of a
lw $t1 0($t1)
sw $t1 -16($sp)
li $t1 4
sw $t1 -20($sp)
# update stack pointer
addi $sp $sp -12
# call the function
jal sum
# restore stack pointer
addi $sp $sp 12
# restore t regs
lw $t0 -12($sp)
# restore ra
move $ra $t0
# get return value off the stack
lw $t0 -24($sp)
move $a0 $t0
li $v0 1
syscall
la $a0 newline
li $v0 4
syscall

# println
# calling function sum
# store ra
move $t0 $ra
# store t registers
sw $t0 -12($sp)
# Evaluate args and place on the stack
# get a offset from the stack pointer.
li $t1 -4
# load offset + sp to get the address of a
add $t1 $sp $t1
# load the value of a
lw $t1 0($t1)
sw $t1 -16($sp)
li $t1 8
sw $t1 -20($sp)
# update stack pointer
addi $sp $sp -12
# call the function
jal sum
# restore stack pointer
addi $sp $sp 12
# restore t regs
lw $t0 -12($sp)
# restore ra
move $ra $t0
# get return value off the stack
lw $t0 -24($sp)
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
datalabel0:	.asciiz "This should print 6 and 28"
