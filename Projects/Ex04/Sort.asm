// File name: Sort.asm

//   |==================================================|
//   |	  	Exercise #  :	 4							|
//   |													|
//   |   	File name   :	 Sort.asm					|
//   |		Date		:	 26/04/2015    	      		|
//   |		Author    	:	 Shai Pe'er        			|
//   |		Email     	:	 shaip86@gmail.com 			|
//   |==================================================|

// Sorts R0 ..... R15 in descending order (4,51,6,12 ===>  51,12,6,4)
// (R0, R1, R2.... refer to RAM[0], RAM[1], and RAM[2]...., respectively.)

	//===================================
	@16
	D=A
    @i
	M=D
	
(LOOP1)	           //while(i < 15)	
	@i
	M=M-1	//i--

(START_LOOP2)
	@16
	D=A
    @j
	M=D

(LOOP2)
	@j
	M=M-1	//j--
	
	@j		//A = j
	A=M-1	//A = j-1
	D=M		//D = M[j-1]
	
	@j		//A = j
	A=M
	D=D-M	//D = M[j-1] - M[j]
	
	@END_SWAP
	D;JGT   // If D > 0 go to @END_SWAP
	@SWAP
	0;JMP	//goto SWAP
	
(SWAP)
	@j		//
	A=M		//A = j
	A=A-1	//A--
	D=M		//D = M[j]
	@temp	//A = temp
	M=D		//temp = M[j-i]
	
	@j		//
	A=M		//A = j
	D=M		//D = M[j]
	@j		//
	A=M		//A = j
	A=A-1	//A--
	M=D		//M[j-1] = M[j]
	
	@temp	//A = temp-loc
	D=M		//D = temp
	@j		//A = M[j]
	A=M		//A = j
	M=D		//M[j] = temp
	
(END_SWAP)

	@j
	D=M-1
	@END_LOOP2
	D;JEQ	//if j = 0 go to END_LOOP2
	@LOOP2
	0;JMP	//goto LOOP2
	
(END_LOOP2)
	@i
	D=M
	@END_LOOP1
	D;JEQ	//if i = 0 go to END_LOOP2
	@LOOP1
	0;JMP	//goto LOOP1

(END_LOOP1)
		
(END)
	@END 
	0;JMP   // Infinite loop

	
