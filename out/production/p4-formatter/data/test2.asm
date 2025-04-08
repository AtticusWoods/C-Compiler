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
#  println
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
li $t0 7
move $a0 $t0
li $v0 1
syscall
la $a0 newline
li $v0 4
syscall
# println
li $t0 3
li $t1 4
add $t2 $t0 $t1
move $a0 $t2
li $v0 1
syscall
la $a0 newline
li $v0 4
syscall
# println
li $t1 14
li $t2 2
div $t1 $t2
mflo $t3
move $a0 $t3
li $v0 1
syscall
la $a0 newline
li $v0 4
syscall
# println
li $t2 7
li $t3 1
mult $t2 $t3
mflo $t4
move $a0 $t4
li $v0 1
syscall
la $a0 newline
li $v0 4
syscall
# println
li $t3 7
li $t4 2
mult $t3 $t4
mflo $t5
li $t4 2
div $t5 $t4
mflo $t6
move $a0 $t6
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
datalabel0:	.asciiz	"This program prints 7 7 7 7 7 (separated by newlines)"
