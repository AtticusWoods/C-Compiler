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
#  a
#  println
#  b
#  return
# Update the stack pointer.
addi $sp $sp -8
# println
la $a0 label_0
li $v0 4
syscall
la $a0 newline
li $v0 4
syscall
# Get a's offset from $sp from the symbol table and initialize a's address with it. We'll add $sp later.
li $t0 -4
# Add the stack pointer address to the offset.
add $t0 $t0 $sp
# Compute rhs for assignment =
li $t1 3
# complete assignment statement with store
sw $t1 0($t0)
# Get b's offset from $sp from the symbol table and initialize b's address with it. We'll add $sp later.
li $t0 -8
# Add the stack pointer address to the offset.
add $t0 $t0 $sp
# Compute rhs for assignment =
li $t2 2
# complete assignment statement with store
sw $t2 0($t0)
# Get a's offset from $sp from the symbol table and initialize a's address with it. We'll add $sp later.
li $t0 -4
# Add the stack pointer address to the offset.
add $t0 $t0 $sp
# Compute rhs for assignment =
li $t3 5
# complete assignment statement with store
sw $t3 0($t0)
# println
# Get a's offset from $sp from the symbol table and initialize a's address with it. We'll add $sp later.
li $t0 -4
# Add the stack pointer address to the offset.
add $t0 $t0 $sp
# Load the value of a.
lw $t4 0($t0)
# Get b's offset from $sp from the symbol table and initialize b's address with it. We'll add $sp later.
li $t0 -8
# Add the stack pointer address to the offset.
add $t0 $t0 $sp
# Load the value of b.
lw $t5 0($t0)
add $t4 $t4 $t5
move $a0 $t4
li $v0 1
syscall
la $a0 newline
li $v0 4
syscall
# Get b's offset from $sp from the symbol table and initialize b's address with it. We'll add $sp later.
li $t0 -4
# Add the stack pointer address to the offset.
add $t0 $t0 $sp
# Compute rhs for assignment =
li $t4 9
# complete assignment statement with store
sw $t4 0($t0)
# Get a's offset from $sp from the symbol table and initialize a's address with it. We'll add $sp later.
li $t0 -4
# Add the stack pointer address to the offset.
add $t0 $t0 $sp
# Compute rhs for assignment =
# complete assignment statement with store
sw null 0($t0)
# println
# Get a's offset from $sp from the symbol table and initialize a's address with it. We'll add $sp later.
li $t0 -4
# Add the stack pointer address to the offset.
add $t0 $t0 $sp
# Load the value of a.
lw $t5 0($t0)
# Get b's offset from $sp from the symbol table and initialize b's address with it. We'll add $sp later.
li $t0 -4
# Add the stack pointer address to the offset.
add $t0 $t0 $sp
# Load the value of b.
lw $t6 0($t0)
add $t5 $t5 $t6
move $a0 $t5
li $v0 1
syscall
la $a0 newline
li $v0 4
syscall
# Get b's offset from $sp from the symbol table and initialize b's address with it. We'll add $sp later.
li $t0 -8
# Add the stack pointer address to the offset.
add $t0 $t0 $sp
# Compute rhs for assignment =
li $t5 4
# complete assignment statement with store
sw $t5 0($t0)
# println
# Get a's offset from $sp from the symbol table and initialize a's address with it. We'll add $sp later.
li $t0 -4
# Add the stack pointer address to the offset.
add $t0 $t0 $sp
# Load the value of a.
lw $t6 0($t0)
# Get b's offset from $sp from the symbol table and initialize b's address with it. We'll add $sp later.
li $t0 -8
# Add the stack pointer address to the offset.
add $t0 $t0 $sp
# Load the value of b.
lw $t7 0($t0)
add $t6 $t6 $t7
move $a0 $t6
li $v0 1
syscall
la $a0 newline
li $v0 4
syscall
# Exiting scope.
addi $sp $sp 8
li $v0 10
syscall

# All memory structures are placed after the
# .data assembler directive
.data

newline:	.asciiz	"\n"
label_0:	.asciiz	"This program prints 7 7 7"
