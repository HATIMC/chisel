# chisel
Custom REST services host.

Host pojo files and run as rest services

go through code to set up environment.

or:
Add CHISEL_PROPERTIES env variable to point to properties file which contains :
CHISEL_TASK_MAPPER_FILE=<path to mapper file>
CHISEL_JAR_DIR=<directory path>
CHISEL_LOGGER=<logger properties file path>

1. CHISEL_TASK_MAPPER_FILE = dont create a file, it will create automatically, just give a file path to it.
2. CHISEL_JAR_DIR = give a directory name which contains all the jars which are to be loaded on startup
3. CHISEL_LOGGER = simple log4j properties file.

its basically a host, without gui, based on rest calls only, gui is a type of beta. search for chiselgui.
