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

# get a offset from the stack pointer.
li $t0 -4
# load offset + sp to get the address of a
add $t0 $sp $t0
# compute rhs for assignment
li $t1 0
# complete assignment by storing rhs in address
sw $t1 0($t0)

# println
# get a offset from the stack pointer.
li $t0 -4
# load offset + sp to get the address of a
add $t0 $sp $t0
# load the value of a
lw $t0 0($t0)
move $a0 $t0
li $v0 1
syscall
la $a0 newline
li $v0 4
syscall

# get a offset from the stack pointer.
li $t0 -4
# load offset + sp to get the address of a
add $t0 $sp $t0
# compute rhs for assignment
li $t1 2
# complete assignment by storing rhs in address
sw $t1 0($t0)

# println
# get a offset from the stack pointer.
li $t0 -4
# load offset + sp to get the address of a
add $t0 $sp $t0
# load the value of a
lw $t0 0($t0)
move $a0 $t0
li $v0 1
syscall
la $a0 newline
li $v0 4
syscall

# get i offset from the stack pointer.
li $t0 -8
# load offset + sp to get the address of i
add $t0 $sp $t0
# compute rhs for assignment
li $t1 2
# complete assignment by storing rhs in address
sw $t1 0($t0)

# get a offset from the stack pointer.
li $t0 -4
# load offset + sp to get the address of a
add $t0 $sp $t0
# compute rhs for assignment
# get i offset from the stack pointer.
li $t1 -8
# load offset + sp to get the address of i
add $t1 $sp $t1
# load the value of i
lw $t1 0($t1)
# complete assignment by storing rhs in address
sw $t1 0($t0)

# println
# get a offset from the stack pointer.
li $t0 -4
# load offset + sp to get the address of a
add $t0 $sp $t0
# load the value of a
lw $t0 0($t0)
move $a0 $t0
li $v0 1
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
# get a offset from the stack pointer.
li $t0 -4
# load offset + sp to get the address of a
add $t0 $sp $t0
# load the value of a
lw $t0 0($t0)
move $a0 $t0
li $v0 1
syscall
la $a0 newline
li $v0 4
syscall

# println
# get a offset from the stack pointer.
li $t0 -4
# load offset + sp to get the address of a
add $t0 $sp $t0
# load the value of a
lw $t0 0($t0)
move $a0 $t0
li $v0 1
syscall
la $a0 newline
li $v0 4
syscall

# println
# get a offset from the stack pointer.
li $t0 -4
# load offset + sp to get the address of a
add $t0 $sp $t0
# load the value of a
lw $t0 0($t0)
li $t1 6
mult $t0 $t1
mflo $t0
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
datalabel0:	.asciiz "This should print 0, 2, 2, 3, 6 and 36"
