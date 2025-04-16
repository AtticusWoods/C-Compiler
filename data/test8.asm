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
#  x
#  y
#  main
#  add2
#  return
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
sw $t1, 0($sp)
move $v0, $t1
jr $ra
# Exiting scope.
addi $sp $sp 0
jr $ra

# code for add2
add2:
# Entering a new scope.
# Symbols in symbol table:
#  add
#  println
#  x
#  y
#  main
#  return
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
sw $t3 -20($sp)
# Get y's offset from $sp from the symbol table and initialize y's address with it. We'll add $sp later.
li $t2 -8
# Add the stack pointer address to the offset.
add $t2 $t2 $sp
# Load the value of y.
lw $t3 0($t2)
sw $t3 -24($sp)
# Update the stack pointer
add $sp $sp -20
# Call the function
jal add
# Restore the stack pointer
add $sp $sp 20
# Restore $t0-9 registers
lw $t0 -8($sp)
lw $t1 -4($sp)
# Restore $ra
move $ra $t1
# Looking for function: add
# Found function add with return type int
# Get return value off stack
lw $t1 -16($sp)
move $t2, $v0
sw $t2 -16($sp)
li $t2 1
sw $t2 -20($sp)
# Update the stack pointer
add $sp $sp -16
# Call the function
jal add
# Restore the stack pointer
add $sp $sp 16
# Restore $t0-9 registers
lw $t1 -8($sp)
lw $t0 -4($sp)
# Restore $ra
move $ra $t0
# Looking for function: add
# Found function add with return type int
# Get return value off stack
lw $t0 -16($sp)
sw $t0, 0($sp)
move $v0, $t0
jr $ra
# Exiting scope.
addi $sp $sp 0
jr $ra

# code for main
main:
# Entering a new scope.
# Symbols in symbol table:
#  add
#  println
#  x
#  y
#  add2
#  return
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
sw $t1 -8($sp)
# Evaluate parameters and save to stack
li $t2 2
sw $t2 -20($sp)
li $t2 4
sw $t2 -24($sp)
# Update the stack pointer
add $sp $sp -20
# Call the function
jal add2
# Restore the stack pointer
add $sp $sp 20
# Restore $t0-9 registers
lw $t1 -8($sp)
lw $t0 -4($sp)
# Restore $ra
move $ra $t0
# Looking for function: add2
# Found function add2 with return type int
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
