MZGlass Z80 assembler
===================

Project information
-------------------

This project is a fork from Glass Assembler. Copyright 2013 Laurens Holst (laurens@grauw.nl).
The original project is available at <https://github.com/grauw/glass>. The license is [Simplified BSD License](https://opensource.org/license/bsd-2-clause).

MZGlass is a cross-assembler for the Z80 processor with special features for the Sharp MZ 700.
It is written in Java 25. Its core
principles are to be open source, cross-platform, and to provide a standard Z80
syntax infused with modern language features.

It presents a flexible language for Z80 object code generation by building an
abstract syntax tree with strong scoping rules, rather than using the
traditional multi-pass architecture with separate preprocessor and mnemonic
translation. This allows the user to write powerful expressions, use macros as
type definitions, etc. Future developments aim to bring more modern programming
concepts to the Z80 assembly programming realm.

Because the binary is a jar which runs on the Java virtual machine, it can be
included in a project easily without requiring the user to acquire a separate
binary build for their operating system.

Usage
-----

To run Glass from the command line, use the following command.

    java -jar glass.jar [OPTION] SOURCE [OBJECT] [SYMBOL]

Source specifies the source file, object the output file, and symbol a text file
which will hold a list of symbols and their addresses in the output.

Options:

  * `-I include_path` Include path for additional source files.
    
  * `-L list_file` File to output a listing of the assembled code to.

  * `-D debug_file` File to output debug information to. This file is used by the MZ700 Debugger.

  * `-O type` Output type. Supported values are `BIN` for a raw binary file
    (the default), and `MZF` for a Sharp MZ-700 program file.

Note that [Java 25](http://java.com/getjava) must be installed to run Glass.
To check your Java version, invoke the `java -version` command.

Syntax
------

The assembler syntax follows common style.

Lines are composed as follows:

    label: mnemonic arguments ;comment

Note that the white space before the mnemonic is significant; otherwise, it will
be interpreted as a label.

All identifiers are case-sensitive. Mnemonics of built-in instructions,
directives, registers, flags and annotations are recognised in lowercase and
uppercase, but can not be mixed case.

Labels
------

Labels and other identifiers follow the following grammar:

    identifier = id_start_char id_char*
    id_start_char = [a-z] | [A-Z] | _ | . | ? | @
    id_char = id_start_char | [0-9] | ' | $

The colon after a label is optional. If a label has no colon, it can not have
any leading white space, it must start at column 0.

Instructions
------------

Standard z80 instruction syntax is used:

    ld a,(ix + 10)

Parentheses are used to indicate indirection.

  * Z80 instructions: `adc`, `add`, `and`, `bit`, `call`, `ccf`, `cp`, `cpd`,
    `cpdr`, `cpi`, `cpir`, `cpl`, `daa`, `dec`, `di`, `djnz`, `ei`, `ex`, `exx`,
    `halt`, `im`, `in`, `inc`, `ind`, `indr`, `ini`, `inir`, `jp`, `jr`, `ld`,
    `ldd`, `lddr`, `ldi`, `ldir`, `neg`, `nop`, `or`, `otdr`, `otir`, `out`,
    `outd`, `outi`, `pop`, `push`, `res`, `ret`, `reti`, `retn`, `rl`, `rla`,
    `rlc`, `rlca`, `rld`, `rr`, `rra`, `rrc`, `rrca`, `rrd`, `rst`, `sbc`,
    `scf`, `set`, `sla`, `sra`, `srl`, `sub`, `xor`
    
    For a complete description of the Z80 instruction set, see the official
    Zilog documentation: <http://www.zilog.com/docs/z80/um0080.pdf>
    
    In addition to the documented Z80 instructions, the variations using the
    undocumented `ixh`, `ixl`, `iyh` and `iyl` index registers are supported, as
    well as the semi-documented `in (c)`.
    
    For register jumps, `jp (hl)` etc., the parentheses are optional.
    
  * R800 instructions: `mulub`, `muluw`
    
    R800 multiplication instructions.
    
  * Define byte: `db`
    
    Defines a byte or a sequence of bytes.

  * Define MZ-700 text: `mz_asc`
    
    Defines bytes like `db`, but maps lowercase ASCII characters to the
    Sharp MZ-700 lowercase character codes.

  * Define MZ-700 display text: `mz_disp`
    
    Defines bytes like `mz_asc`, but maps characters to the display codes used
    when writing directly to video memory.
    
  * Define word: `dw`
    
    Defines a word or a sequence of words.
    
  * Define double word: `dd`
    
    Defines a double word or a sequence of double words.
    
  * Define space: `ds`
    
    Defines space for a number of bytes. The first argument indicates the number
    of bytes, the optional second argument specifies the fill value (default 0).
    
    The first argument can be annotated with `virtual`, in which case the
    address counter will be incremented accordingly, but no object is actually
    generated in the output. If the virtual annotation is given, you can not
    specify a fill value.

Directives
----------

#### `org`: Origin
  
The `org` is the only directive that is not an instruction.
Changes the address location counter and sets a new origin for subsequent
statements.

    org $100

#### `phase`, `dephase`: Phased code
    
Temporarily changes the address used for labels and `$`, without advancing
the surrounding address location counter. Object code is still emitted at
the current output position.

This is useful for code that is stored in one place in the output file, but
will be copied, banked, or mapped to another address before it is executed.
    
        org $1200
        nop
        nop
 
        phase $3000
    label:
        ld hl,label
        dephase

    label2:
        ld bc,label2

In this example, the two `nop` instructions are assembled at `$1200` and
`$1201`. The phased block is emitted next in the output file, but labels
inside it are resolved as if the code starts at `$3000`, so `ld hl,label`
loads `$3000`. After `dephase`, assembly resumes at the original address,
so `label2` is `$1202`.

A phase block can also specify a maximum size:

        phase $3000,$100
        ; phased code here must fit in $100 bytes
        dephase

If the phased code exceeds the limit before `dephase`, assembly fails. It is
also an error to start a new `phase` or bank block before ending the current
one.

#### `bank_a`, `bank_b`, and `bank_c`: use with the Sharp MZ700 ST-110 MMU board

These are aliases for phased blocks starting at `$D000`, `$E010`, and `$F000`, respectively.
`bank_a` and `bank_c` default to a maximum size of 4096 bytes, and `bank_b` defaults to 4080
bytes. A numeric argument overrides the default size, and `size_off` disables the size limit.
`end_bank` is an alias for `dephase`.

        bank_a
    BankRoutine:
        ld hl,BankRoutine
        ret
        end_bank

        bank_b 512
        ; this bank block must fit in 512 bytes
        end_bank

        bank_c size_off
        ; no explicit size limit
        end_bank

#### `mzf_title`, `mzf_load`, `mzf_start`, `mzf_comments`: MZF output metadata
    
Specifies metadata for `-O MZF` output.\
`mzf_load` is required. `mzf_start` defaults to the load address, `mzf_title` defaults to `TITLE`, and
`mzf_comments` defaults to an empty field. If these directives appear multiple times, the last value is used.
    
        mzf_title the game!
        mzf_load $1200
        mzf_start $1200
        mzf_comments comments
    
#### `equ`: Assign constant
    
Assigns a constant value to a symbol.
    
        JIFFY: equ $FC9E
    
#### `include`: Include
    
Includes another source file. The current working directory is searched, as
well as any include paths specified on the command line.
    
        INCLUDE "math.asm"
    
Optionally you can specify a `once` annotation to prevent a file from being
included more than once. However it is not recommended to use unless needed.
    
        INCLUDE ONCE "math.asm"
    
#### `incbin`: Include binary
    
Includes binary data from a file. The current working directory is searched,
as well as any include paths specified on the command line.
    
        INCBIN "image.ge5"
    
Optionally you can specify a start position and length:
    
        INCBIN "image.ge5",7,212*128
    
#### `macro`, `endm`: Macros
    
Defines a macro instruction, composed of all the instructions that follow
until the `endm` directive is encountered. The definition’s arguments
specify the parameters which are passed when the macro is invoked.
    
        ALIGN: MACRO ?boundary
               ds ?boundary - 1 - ($ + ?boundary - 1) % ?boundary
               ENDM
               
               ALIGN 100H
    
All symbols defined in a macro block are local. Symbols in macro instances
can be referenced by using the `.` operator.
    
Default values for macro arguments can be specified with `=`:
    
        ALIGN: MACRO ?boundary = 100H

Macros can be used to define convenient pseudo-instructions. Arguments are
expressions, so registers, labels, constants, and parenthesized operands can
be passed through to the expanded code.

        LDW: MACRO dest,src
             push src
             pop dest
             ENDM

             LDW bc,hl       ; expands to: push hl / pop bc

Macro parameters can also describe a required parenthesized argument. The
parentheses are matched at the call site, and the inner expression is bound
to the parameter:

        LDB: MACRO dest,(src)
             push hl
             LDW hl,src
             ld dest,(hl)
             pop hl
             ENDM

             LDB c,(de)

In this example, `dest` is `c`, and `src` is `de`. Calling `LDB c,de`
would be rejected because the second argument is required to be
parenthesized.

Macro arguments can be tested with `if`. Register arguments can be compared
with `=` and `!=`, which is useful for validating pseudo-instruction
operands:

        ADDB: MACRO dest,src
             IF dest = a
                 ERROR "Invalid destination. A is not allowed."
             ELSE
                 ld (SAVE_A),a
                 ld a,dest
                 add a,src
                 ld dest,a
                 ld a,(SAVE_A)
             ENDIF
             ENDM

             ADDB c,e

        SAVE_A: ds 1

Macro definitions do not define structure offsets. Use `struct` for data
layouts.

#### `struct`, `endstruct`: Structures

Defines a structure layout. The contents are assembled virtually at address
0, turning the inner symbols into offsets. The structure itself does not
emit object code.

        PLAYER: STRUCT
        x:       db 0
        y:       db 0
        health:  db 0
        score:   dw 0
        _size:   ENDSTRUCT

The labels inside the structure become offsets:

        PLAYER.x       ; 0
        PLAYER.y       ; 1
        PLAYER.health  ; 2
        PLAYER.score   ; 3
        PLAYER._size   ; 5

You can allocate storage for an instance and access fields by adding the
offsets to the instance address:

    player1:
        ds PLAYER._size

        ld hl,player1 + PLAYER.health
        ld (hl),100

If an index register points to an instance, the offsets can be used directly
in indexed addressing:

        ld ix,player1
        ld a,(ix + PLAYER.x)
        ld b,(ix + PLAYER.y)
        ld (ix + PLAYER.health),100
    
#### `rept`, `endm`: Repetition
    
Repeats a section of code a number of times. The end of the section is
marked with the `endm` directive. The first argument is mandatory and
specifies the number of repeats. The second argument specifies a counter
parameter, the third the initial value for the counter (default: 0), and the
fourth argument specifies the counter increment (default: 1).
    
        REPT 10, ?counter, 0, 2
        ld bc,(table + ?counter)
        REPT 3
        add hl,bc
        ENDM
        ENDM
    
All symbols defined in a repeat block are local. If a repeat is labeled,
the inner repeat scopes can be accessed by index, e.g.: `mylist.0`.
    
#### `irp`, `endm`: Indefinite repetition
    
Repeats a section of code for each of the arguments specified. The end of
the section is marked with the `endm` directive. The first argument is
mandatory and specifies the parameter the current repetition’s value is
passed to. The remaining arguments are passed one by one as the section is
repeated.
    
        IRP ?value, 1, 2, 4, 8, 16, 32, 64, 128
        or ?value
        ENDM
    
All symbols defined in a indefinite repeat block are local. If a repeat is
labeled, the inner repeat scopes can be accessed by index, e.g.: `mylist.0`.
    
#### `proc`, `endp`:  Procedure
    
Defines a section of code as a procedure. Currently mostly serves to
establish a local scope.
    
        shift5: PROC
                ld b,5
                jp shiftl.loop
                ENDP
                
        shiftl: PROC
                ld b,1
        loop:   add a,a
                djnz loop
                ret
                ENDP
    
All symbols defined in a procedure block are local. Symbols in inner scopes
can be referenced by using the `.` operator.
    
#### `if`, `else`, `endif`: Condition
    
Conditionally assembles a section of code, or an optional alternative
section. The end of the section is either marked with `endif`, or with
`else` in which case an alternative will follow up to the `endif`. The
argument is evaluated as an integer, and if the result is nonzero (true) the
first section is assembled, and if the result is zero (false) the
alternative is assembled if one is provided.
    
        PAD: MACRO ?address
             IF $ > ?address
                 ERROR "Padding address exceeded."
             ELSE
                 ds ?address - $
             ENDIF
             ENDM
    
#### `end`: Source file end
    
Indicates the end of the current source file. Specifying this is optional,
and usually omitted. Any content on lines beyond this directive will not be
parsed.
    
#### `error`: Error
    
Generates an error and aborts the compilation. Optionally a message can be
specified.
    
        ERROR "Limit exceeded."
    
#### `warning`: Warning
    
Generates a warning. Optionally a message can be specified.
    
        WARNING "Nearly out of space."
    
#### `section`: Section
    
Defines a section of code or data that will be assembled inside the space of
a ds statement. This allows you to have nonadjacent code or data sections
and group them into separate regions, such as ROM and RAM pages. The
mandatory argument references the DS statement that is the target of the
section.
    
            org 4000H
        ROM_PAGE1: ds 4000H
        ROM_PAGE2: ds 4000H
        RAM: ds VIRTUAL 3000H
        
            SECTION ROM_PAGE1
        SetValue:
            ld (value),a
            ret
        
            SECTION RAM
        value: db 0
            ENDS
            ENDS

Literals
--------

  * Decimal: `127`
  * Hexadecimal: `0FC9EH`, `#FC9E`, `$FC9E` or `0xFC9E`
  * Binary: `10110001B` or `%10110001`
  * Octal: `377O`
  * Character: `'c'`
  * String: `"abc"`

Character literals can contain the `'` character by repeating it as `''`, and
string literals can contain the `"` character by repeating it as `""`.

Character and string literals support the following escape sequences:

  * `\0` (NUL)
  * `\a` (bell)
  * `\t` (tab)
  * `\n` (line feed)
  * `\f` (form feed)
  * `\r` (carriage return)
  * `\e` (escape)
  * `\"` (double quotation mark)
  * `\'` (single quotation mark)
  * `\\` (backslash)

Numeric escape sequences are not supported. In stead, you can insert them using
the comma operator: `"abc", 0FFH, "def"`.

The character set used to read files is ISO-8859-1, this maps the file’s bytes
1:1 to the Unicode code points used internally so the object code output matches
the input file bytes verbatim.

The assembler uses 32-bit integer math internally. When a 8-bit or 16-bit value
is generated, the excessive bits are usually truncated. Except for addresses,
used in jumps, calls and indirect loads, they generate an error. Index and
relative jump offsets are also checked to be in their allowed range.

Operators
---------

  * Member: `.`
  * Positive: `+a`
  * Negative: `-a`
  * Complement: `~a`
  * Not: `!a`
  * Multiply: `a * b`
  * Divide: `a / b`
  * Modulo: `a % b`
  * Add: `a + b`
  * Subtract: `a - b`
  * Shift left: `a << b`
  * Shift right: `a >> b`
  * Shift right unsigned: `a >>> b`
  * Less than: `a < b`
  * Less or equal: `a <= b`
  * Greater than: `a > b`
  * Greater or equal: `a >= b`
  * Equal: `a = b`
  * Not equal: `a != b`
  * Bitwise and: `a & b`
  * Bitwise xor: `a ^ b`
  * Bitwise or: `a | b`
  * Logical and: `a && b`
  * Logical or: `a || b`
  * Ternary if: `a ? b : c`
  * Annotation: `a b`
  * Sequence: `a, b`
  * Group: `()`
  * Index: `[]`

Logical operators use integers to represent true / false values. 0 means false,
any other value means true. They return -1 for true values.

Logical and / or apply short-circuit evaluation and evaluate to the last
evaluated value, so they can also be used similar to a ternary operator.

Expressions can span multiple lines when they’re incomplete at the line ends.

Operator precedence:

  1. `.`
  2. unary `+` `-` `~` `!`
  3. `*` `/` `%`
  4. `+` `-`
  5. `<<` `>>` `>>>`
  6. `<` `<=` `>` `>=`
  7. `=` `!=`
  8. `&`
  9. `^`
  10. `|`
  11. `&&`
  12. `||`
  13. `?:`
  14. ` `
  15. `,`

Development information
-----------------------

MZGlass is free and open source software. If you want to contribute to the project
you are very welcome to. Please contact me at any one of the places mentioned in
the project information section.

You are also free to re-use code for your own projects, provided you abide by
the license terms.
