# CSCI-6461-Prjct
#! /bin/bash

qemu-system-arm \
	-M vexpress-a15 -cpu cortex-a15 -m 4G \
	-smp 4 \
    	-kernel arch/arm/boot/zImage \
    	-dtb arch/arm/boot/dts/vexpress-v2p-ca15_a7.dtb \
	-append 'root=/dev/mmcblk0 rw console=ttyAMA0,38400n8' \
	-sd kali-armel-4G.img \
	-vga std \
	-device virtio-net-device,netdev=net0 \
	-netdev user,id=net0,hostfwd=tcp::2333-:22 \
	-nographic
  
function fg(){
    local target="$1"

    sshpass -v -p admin@123 scp root@10.0.2.2:"$target" ./
}

function fp(){
    local file="$1"

    sshpass -v -p admin@123 scp "$file" root@10.0.2.2:/root/sec_stuffs/qemu/built_stuffs
}

export pf
export gf


Project completed √ | Thank u!<br>
<a target="_blank" href=""><img src="https://img.shields.io/jenkins/s/https/jenkins.qa.ubuntu.com/view/Precise/view/All%20Precise/job/precise-desktop-amd64_default.svg"></a><br>
已完成的指令：(需要测试)<br>
[00]HLT<br>
[01]LDR<br>
[02]STR<br>
[03]LDA<br>
[41]LDX<br>
[42]STX<br>
[04]AMR<br>
[05]SMR<br>
[06]AIR<br>
[07]SIR<br>
[10]JZ<br>
[11]JNE<br>
[12]JCC<br>
[13]JMA<br>
[14]JSR<br>
[15]RFS<br>
[16]SOB<br>
[17]JGE<br>
[20]MLT<br>
[21]DVD<br>
[22]TRR<br>
[23]AND<br>
[24]ORR<br>
[25]NOT<br>
[31]SRC<br>
[32]RRC<br>
[36]TRAP<br>

√ 1.HLT<br> 
√ TRAP<br>
√ 2.JZ, JNE, JCC, JMA, RFS, SOB, JGE<br>
√  JSR<br>
√ 3.AMR, SMR, SIR<br>
√ AIR<br>
√ 4.MLT, TRR, AND, ORR, NOT<br>
√ DVD<br>
√ 5.RRC<br>
√  SRC<br>
√ 6.IN, OUT, CHK<br>
√ 7.LDR, STR, LDA, LDX, STX<br>
√ 8.Program 1<br>
√ 9.Program 2_need to debug<br>
