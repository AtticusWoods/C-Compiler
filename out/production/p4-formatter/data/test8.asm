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
li $t2 -8
# Add the stack pointer address to the offset.
add $t2 $t2 $sp
# Load the value of y.
lw $t3 0($t2)
add $t1 $t1 $t3
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
#  x
#  y
#  return
# Update the stack pointer.
addi $sp $sp -0
# Calling function add
# Save $ra to a dedicated register
move $t0 $ra
# Save used temporary registers
sw $t0 -8($sp)
# Evaluate parameters and save to stack
# Calling function add
# Save $ra to a dedicated register
move $t1 $ra
# Save used temporary registers
sw $t0 -20($sp)
sw $t1 -24($sp)
# Evaluate parameters and save to stack
# Get x's offset from $sp from the symbol table and initialize x's address with it. We'll add $sp later.
li $t2 -4
# Add the stack pointer address to the offset.
add $t2 $t2 $sp
# Load the value of x.
lw $t3 0($t2)
sw $t3 -28($sp)
# Get y's offset from $sp from the symbol table and initialize y's address with it. We'll add $sp later.
li $t3 -8
# Add the stack pointer address to the offset.
add $t3 $t3 $sp
# Load the value of y.
lw $t4 0($t3)
sw $t4 -32($sp)
# Update the stack pointer
add $sp $sp -24
# Call the function
jal add
# Restore the stack pointer
add $sp $sp 24
# Restore used temporary registers
lw $t0 -16($sp)
# Restore $ra
move $ra $t1
# Get return value off stack
lw $t0 -36($sp)
sw $t0 -12($sp)
li $t0 1
sw $t0 -4($sp)
# Update the stack pointer
add $sp $sp -8
# Call the function
jal add
# Restore the stack pointer
add $sp $sp 8
# Restore used temporary registers
# Restore $ra
move $ra $t0
# Get return value off stack
lw $t0 -20($sp)
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
#  return
# Update the stack pointer.
addi $sp $sp -0
# println
la $a0 label_0
li $v0 4
syscall
la $a0 newline
li $v0 4
syscall
# println
# Calling function add2
# Save $ra to a dedicated register
move $t0 $ra
# Save used temporary registers
sw $t0 -8($sp)
sw $t2 -12($sp)
sw $t3 -16($sp)
# Evaluate parameters and save to stack
li $t1 2
sw $t1 -20($sp)
li $t1 4
sw $t1 -24($sp)
# Update the stack pointer
add $sp $sp -16
# Call the function
jal add2
# Restore the stack pointer
add $sp $sp 16
# Restore used temporary registers
lw $t2 -12($sp)
lw $t3 -16($sp)
# Restore $ra
move $ra $t0
# Get return value off stack
lw $t0 -28($sp)
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
label_0:	.asciiz	"This program prints 7"
