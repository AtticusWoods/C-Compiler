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
move $t0 $t1
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
# Calling function add
# Save $ra to a dedicated register
move $t0 $ra
# Save used temporary registers
# Get x's offset from $sp from the symbol table and initialize x's address with it. We'll add $sp later.
li $t0 -4
# Add the stack pointer address to the offset.
add $t0 $t0 $sp
# Load the value of x.
lw $t1 0($t0)
# Get y's offset from $sp from the symbol table and initialize y's address with it. We'll add $sp later.
li $t1 -8
# Add the stack pointer address to the offset.
add $t1 $t1 $sp
# Load the value of y.
lw $t2 0($t1)
# Evaluate parameters and save to stack
sw $t1 -4($sp)
sw $t2 -8($sp)
# Update the stack pointer
add $sp $sp -0
# Call the function
jal add
# Restore the stack pointer
add $sp $sp 0
# Restore $ra
move $ra $t0
li $t0 1
# Evaluate parameters and save to stack
sw $t0 -4($sp)
sw $t0 -8($sp)
# Update the stack pointer
add $sp $sp -0
# Call the function
jal add
# Restore the stack pointer
add $sp $sp 0
# Restore $ra
move $ra $t0
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
sw $t1 -4($sp)
li $t0 2
li $t0 4
# Evaluate parameters and save to stack
sw $t0 -8($sp)
sw $t0 -12($sp)
# Update the stack pointer
add $sp $sp -4
# Call the function
jal add2
# Restore the stack pointer
add $sp $sp 4
# Save return value temporarily
move $t1 $t0
# Restore used temporary registers
lw $t1 -4($sp)
# Restore $ra
move $ra $t0
move $t0 $t1
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
