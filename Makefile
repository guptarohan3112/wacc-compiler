# Sample Makefile for the WACC Compiler lab: edit this to build your own comiler
# Locations

ANTLR_DIR	:= compiler/antlr_config
SOURCE_DIR	:= compiler/src/main/java
OUTPUT_DIR	:= compiler/bin
COMPILER	:= compiler

# Tools

ANTLR	:= antlrBuild
FIND	:= find
RM	:= rm -rf
MKDIR	:= mkdir -p
JAVA	:= java
JAVAC	:= javac

JFLAGS	:= -sourcepath $(SOURCE_DIR) -d $(OUTPUT_DIR) -cp compiler/lib/antlr-4.9.1-complete.jar

# the make rules

all: rules

# runs the antlr build script then attempts to compile all .java files within src
rules:
	cd $(ANTLR_DIR) && ./$(ANTLR)
	cd $(COMPILER) && ./gradlew assemble

clean:
	$(RM) rules $(OUTPUT_DIR) $(SOURCE_DIR)/antlr
	$(RM) build

.PHONY: all rules clean


