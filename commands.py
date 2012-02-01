import sys
import inspect
import os
import glob
import re
import subprocess

# Here you can create play commands that are specific to the module, and extend existing commands

MODULE = 'evolutions'

# Commands that are specific to your module

COMMANDS = ['evolutions:generate']

def execute(**kargs):
    command = kargs.get("command")
    app = kargs.get("app")
    args = kargs.get("args")
    env = kargs.get("env")

    if command == "evolutions:generate":
        java_cmd = app.java_cmd(args, None, "play.modules.evolutions.GenerateMigrationMain")
        try:
            subprocess.call(java_cmd, env=os.environ)
        except OSError:
            print "Could not execute the java executable, please make sure the JAVA_HOME environment variable is set properly (the java executable should reside at JAVA_HOME/bin/java). "
            sys.exit(-1)
        sys.exit(0)


# This will be executed before any command (new, run...)
def before(**kargs):
    command = kargs.get("command")
    app = kargs.get("app")
    args = kargs.get("args")
    env = kargs.get("env")


# This will be executed after any command (new, run...)
def after(**kargs):
    command = kargs.get("command")
    app = kargs.get("app")
    args = kargs.get("args")
    env = kargs.get("env")

    if command == "new":
        pass
