# All program code is placed after the
# .text assembler directive
.text

# Declare main as a global function
.globl	main

j main

# code for add
add:
# Entering a new scope.
# Symbols in symbol table:
#  println
#  x (offset: -4)
#  y (offset: -8)
#  return (offset: -12)
# Update the stack pointer.
addi $sp $sp -0
# Get x's offset from $sp from the symbol table and initialize x's address with it. We'll add $sp later.
li $t0 -4
# Add the stack pointer address to the offset.
add $t0 $t0 $sp
# Load the value of x.
lw $t1 0($t0)
# Get y's offset from $sp from the symbol table and initialize y's address with it. We'll add $sp later.
li $t0 -8
# Add the stack pointer address to the offset.
add $t0 $t0 $sp
# Load the value of y.
lw $t2 0($t0)
add $t1 $t1 $t2
sw $t1 -12($sp)
jr $ra
# Exiting scope.
addi $sp $sp 0
jr $ra

# code for add2
add2:
# Entering a new scope.
# Symbols in symbol table:
#  println
#  x (offset: -4)
#  y (offset: -8)
#  return (offset: -12)
# Update the stack pointer.
addi $sp $sp -0
# Calling function add
# Save $ra to a register
move $t0 $ra
# Save $t0-9 registers
sw $t0 -4($sp)
# Evaluate parameters and save to stack
# Calling function add
# Save $ra to a register
move $t1 $ra
# Save $t0-9 registers
sw $t1 -4($sp)
sw $t0 -8($sp)
# Evaluate parameters and save to stack
# Get x's offset from $sp from the symbol table and initialize x's address with it. We'll add $sp later.
li $t2 -4
# Add the stack pointer address to the offset.
add $t2 $t2 $sp
# Load the value of x.
lw $t3 0($t2)
sw $t3 -8($sp)
# Get y's offset from $sp from the symbol table and initialize y's address with it. We'll add $sp later.
li $t3 -8
# Add the stack pointer address to the offset.
add $t3 $t3 $sp
# Load the value of y.
lw $t4 0($t3)
sw $t4 -12($sp)
# Update the stack pointer
add $sp $sp -12
# Call the function
jal add
# Restore the stack pointer
add $sp $sp 12
# Restore $t0-9 registers
lw $t1 -4($sp)
lw $t0 -8($sp)
# Restore $ra
move $ra $t1
# Get return value off stack
lw $t1 -16($sp)
sw $t1 -8($sp)
li $t1 1
sw $t1 -12($sp)
# Update the stack pointer
add $sp $sp -12
# Call the function
jal add
# Restore the stack pointer
add $sp $sp 12
# Restore $t0-9 registers
lw $t0 -4($sp)
# Restore $ra
move $ra $t0
# Get return value off stack
lw $t0 -16($sp)
sw $t0 -12($sp)
jr $ra
# Exiting scope.
addi $sp $sp 0
jr $ra

# code for main
main:
# Entering a new scope.
# Symbols in symbol table:
#  println
# Update the stack pointer.
addi $sp $sp -0
# println
la $a0 datalabel0
li $v0 4
syscall
la $a0 newline
li $v0 4
syscall
# println
# Calling function add2
# Save $ra to a register
move $t0 $ra
# Save $t0-9 registers
sw $t0 -4($sp)
sw $t1 -12($sp)
sw $t2 -16($sp)
# Evaluate parameters and save to stack
li $t1 2
sw $t1 -8($sp)
li $t1 4
sw $t1 -12($sp)
# Update the stack pointer
add $sp $sp -12
# Call the function
jal add2
# Restore the stack pointer
add $sp $sp 12
# Restore $t0-9 registers
lw $t0 -4($sp)
lw $t1 -12($sp)
lw $t2 -16($sp)
# Restore $ra
move $ra $t0
# Get return value off stack
lw $t0 -16($sp)
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

newline:	.asciiz	"\n"
datalabel0:	.asciiz	"This program prints 7"
