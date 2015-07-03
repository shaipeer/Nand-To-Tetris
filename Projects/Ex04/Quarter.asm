// File name: Quarter.asm

//   |==================================================|
//   |	  	Exercise #  :	 4							|
//   |													|
//   |   	File name   :	 Quarter.asm				|
//   |		Date		:	 26/04/2015    	      		|
//   |		Author    	:	 Shai Pe'er        			|
//   |		Email     	:	 shaip86@gmail.com 			|
//   |==================================================|

// You should split the Screen to four quarters(left/right and up/down) in the following manner: 
//In an  infinite loop: while no key is pressed,the top left and bottom right are black, 
//and top right and bottom left are white. While any key is pressed, the colors are switched. 


//===================== INITIAL =====================
	
	@0
    D=A
    @i
    M=D			//==> i = 0	
	
	@0
    D=A
    @j
    M=D			//==> j = 0
	
	@0
    D=A
    @color
    M=D		
	M=M-1		//==> color = -1 (black)
	
    @SCREEN		//screen 0,0 to A
    D=A			//D=screen 0,0
    @last_pixal_num		//last_pixal_num = screen 0,0
    M=D
	
	@0
    D=A
    @is_key_pressed
    M=D		
	M=M-1		//==> is_key_pressed = -1

//===================== Main Loop =====================
(LOOP)
    
	@IF_I_EQ_16_END
	D=A
	@if_location
	M=D		//if_location = IF_I_EQ_16_END
	
	@i
//	A=M
	D=M		//D = i
	@16
	D=D-A	//D = D-16
	@SWAP_COLOR
	D; JEQ	//if D=0 go to SWAP_COLOR
(IF_I_EQ_16_END)
	
	
	@IF_I_EQ_32_END
	D=A
	@if_location
	M=D		//if_location = IF_I_EQ_32_END
	
	@i
	D=M		//D = i
	@32
	D=D-A	//D = D-32
	@IF_I_EQ_32
	D; JEQ	//if D=0 go to IF_I_EQ_32
(IF_I_EQ_32_END)

	
	@IF_J_EQ_128_END
	D=A
	@if_location
	M=D		//if_location = IF_J_EQ_128_END
	
	@j
	D=M		//D = j
	@128
	D=D-A	//D = D-128
	@IF_J_EQ_128
	D; JEQ	//if D=0 go to IF_J_EQ_128
(IF_J_EQ_128_END)
	
	
	@j
	D=M		//D = j
	@256
	D=D-A	//D = D-256
	@IF_J_EQ_256
	D; JEQ	//if D=0 go to IF_J_EQ_256
(IF_J_EQ_256_END)
	
	
	@DRAW_PIXAL
	0; JMP
	(DRAW_PIXAL_END)
	
	
	@i
    M=M+1	//i++
    @LOOP   
    0; JMP	//go to LOOP
(LOOP_END)

//===================== listen to key =====================
(LISTEN_KEY)
	
	@KBD
    D=M
    @NO_KEY_PRESSED
    D;JEQ			 //if(KBD=0)
    @KEY_PRESSED
    0;JMP
(KEY_PRESSED)
    @0
    D=A
    @color
    M=D		//==> color = 0 (white)
	
	@0
    D=A
    @is_key_pressed
    M=D		//==> is_key_pressed = 0
	
    @LOOP
    0; JMP
	
(NO_KEY_PRESSED)	
	@0
    D=A
    @color
    M=D		
	M=M-1	//==> color = -1 (black)
	
	@is_key_pressed
	D=M
	@LOOP
	D; JEQ	//if D=0 go to SWAP_COLOR
	
	
@LISTEN_KEY
0; JMP



//===================== Draw Pixal (i,j) =====================
(DRAW_PIXAL)
	@color
	D=M
	@last_pixal_num
    A=M
    M=D				//M[last_pixal_num] = color
    @last_pixal_num
    D=M
    @1
    D=D+A
    @last_pixal_num
    M=D				//last_pixal_num++
@DRAW_PIXAL_END
0; JMP

//===================== if i equals 16 =====================
(IF_I_EQ_16)
	

	@IF_I_EQ_16_END
	0; JMP

//===================== if i equals 32 =====================
(IF_I_EQ_32)
	@j
    M=M+1	//j++
	
	@0
    D=A
    @i
    M=D		//==> i = 0
@SWAP_COLOR
0; JMP

//===================== if j equals 128 =====================
(IF_J_EQ_128)
	
	@i
	D=M		//D = i
	@SWAP_COLOR
	D; JEQ	//if D=0 go to IF_I_EQ_32
	
@IF_J_EQ_128_END
0; JMP

//===================== if j equals 256 =====================
(IF_J_EQ_256)
	@0
    D=A
    @i
    M=D		//==> i = 0	
	
	@0
    D=A
    @j
    M=D		//==> j = 0	
	
    @SCREEN
    D=A	
    @last_pixal_num		
    M=D		//last_pixal_num = screen 0,0
	
@LISTEN_KEY
0; JMP

@IF_J_EQ_128_END
D; JMP


//===================== Swap Color =====================
(SWAP_COLOR)
	
	@color
	D=M
	@CHAGE_COLOR_TO_BLACK
	D; JEQ
	
	@0
    D=A
    @color
    M=D		//==> color = 0 (white)
	@SWAP_COLOR_END
	0; JMP

(CHAGE_COLOR_TO_BLACK)
	@0
    D=A
    @color
    M=D		
	M=M-1	//==> color = -1 (black)
	
	@if_location
	D = M
(SWAP_COLOR_END)
	@if_location
	A = M
	0; JMP
	
	
	
	
	
	(FINISH)