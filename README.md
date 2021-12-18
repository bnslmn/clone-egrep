## Introduction

This project is an implementation of a *clone* of the *UNIX* **_egrep_** command as part of the *Master 2 - STL* course *DAAR* at *Sorbonne Université* by Amine Benslimane and Walid Sadat.

October 2021,
Sorbonne Université.

## Algorithms used

For finite RegEx, we use the [KMP Algorithm](https://github.com/bnslmn/clone-egrep/blob/main/documents/kmp_strings.pdf)

For non-finite RegEx, we use the [Aho-Ullman Algorithm](https://github.com/bnslmn/clone-egrep/blob/main/documents/ch10.pdf)

## Run
To run the command, run the **_runEgrep.sh_** *shell script* passing a regular expression and a valid file name as arguments :

```sh
sh runEgrep.sh RegEx FileName
```

Or by using *bash* command :

```sh
bash runEgrep.sh RegEx FileName
```

Or by setting permission on the script using *chmod* command :

```sh
chmod +x runEgrep.sh
./runEgrep RegEx FileName
```

## Experimentation

For RegEx = `al`  in [a french dictionary](https://github.com/bnslmn/clone-egrep/blob/main/tests/liste_francais.txt) , we obtain :

![kpm_result](https://user-images.githubusercontent.com/77028316/146639143-b44c55e2-e2ff-426f-bb09-4df6e44c360c.png)

For RegEx = `S(a|g|r)*on` in [A History of Babylon, From the Foundation of the Monarchy to the Persian Conquest History of Babylonia vol. 2](https://github.com/bnslmn/clone-egrep/blob/main/tests/56667-0.txt) , we obtain :

![algovsegrep](https://user-images.githubusercontent.com/77028316/146639229-eece8aa9-3fac-45da-853e-c85128c9741e.png)

Our Algorithm on the left, the egrep command on the right

We can see the accuracy of our algorithm in cloning the behavior of the egrep command.

See our [document](https://github.com/bnslmn/clone-egrep/blob/main/documents/Clone_egrep.pdf) for more information
