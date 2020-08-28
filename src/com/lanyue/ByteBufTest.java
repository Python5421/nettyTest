package com.lanyue;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;
import java.util.Arrays;

public class ByteBufTest {
    public static void main(String[] args) {
        //初始化ByteBuf，读写索引都为0；
        ByteBuf buf = Unpooled.buffer(10);//非池化10字节大小的ByteBuf
        System.out.println(buf.toString());//UnpooledHeapByteBuf(ridx: 0, widx: 0, cap: 10)
        System.out.println( Arrays.toString(buf.array()));//[0, 0, 0, 0, 0, 0, 0, 0, 0, 0]

        //写入5个字节的数组，写索引变为5；
        byte[] bytes = {1,2,3,4,5};
        buf.writeBytes(bytes);
        System.out.println(buf.toString());//UnpooledHeapByteBuf(ridx: 0, widx: 5, cap: 10)
        System.out.println(Arrays.toString(buf.array()));//[1, 2, 3, 4, 5, 0, 0, 0, 0, 0]

        //读取两个字节，写索引为5，读索引2
        byte b1 = buf.readByte();
        byte b2 = buf.readByte();
        System.out.println(Arrays.toString(new byte[]{b1,b2}));
        System.out.println(buf.toString());//UnpooledHeapByteBuf(ridx: 2, widx: 5, cap: 10)
        System.out.println(Arrays.toString(buf.array()));//[1, 2, 3, 4, 5, 0, 0, 0, 0, 0]

        //将读取的内容丢弃后，读索引0，写索引(5-2)=3
        buf.discardReadBytes();//将读取的内容丢弃
        System.out.println(buf.toString());//UnpooledHeapByteBuf(ridx: 0, widx: 3, cap: 10)
        System.out.println(Arrays.toString(buf.array()));//[3, 4, 5, 4, 5, 0, 0, 0, 0, 0]

        System.out.println(buf.readableBytes());//返回可读字节的个数；  3
        System.out.println(buf.writableBytes());//返回可写字节的个数；  7

//清空读写指针，读写指针都为0，clear并不会清空缓冲区内容本身，主要用来操作指针；
        buf.clear();
        System.out.println(buf.toString());//UnpooledHeapByteBuf(ridx: 0, widx: 0, cap: 10)
        System.out.println(Arrays.toString(buf.array()));//[3, 4, 5, 4, 5, 0, 0, 0, 0, 0]

        //再次写入一段内容
        byte[] bytes2 = {1,2,3};
        buf.writeBytes(bytes2);
        buf.readByte();
        System.out.println(Arrays.toString(bytes2));
        System.out.println(buf.toString());//UnpooledHeapByteBuf(ridx: 1, widx: 3, cap: 10)
        System.out.println(Arrays.toString(buf.array()));//[1, 2, 3, 4, 5, 0, 0, 0, 0, 0]

        // 将ByteBuf清零，读写索引都不变
        buf.setZero(0,buf.capacity());
        System.out.println(buf.toString());//UnpooledHeapByteBuf(ridx: 1, widx: 3, cap: 10)
        System.out.println(Arrays.toString(buf.array()));//[0, 0, 0, 0, 0, 0, 0, 0, 0, 0]


        Charset utf8 = Charset.forName("UTF-8");
        ByteBuf buf1 = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
        ByteBuf sliced = buf1.slice(0, 14);
        ByteBuf copy = buf1.copy(0, 14);
        System.out.println(buf1.toString(utf8));//Netty in Action rocks!
        System.out.println("是否支持访问数组:"+buf1.hasArray());//false  访问非堆缓冲区ByteBuf的数组会导致UnsupportedOperationException,因此下面的访问方式会报错

//System.out.println(Arrays.toString(buf1.array()));
        System.out.println(sliced.toString(utf8));//Netty in Actio
//System.out.println(Arrays.toString(sliced.array()));
        System.out.println(copy.toString(utf8));//Netty in Actio
//System.out.println(Arrays.toString(copy.array()));

        buf1.setByte(0, (byte)'J');//get和set操作读写指定索引的数据，而不会改变索引值。read和write操作读写指定索引数据，并且会改变索引的值
        System.out.println(buf1.toString(utf8));//Jetty in Action rocks!
//由于数据是共享的，对一个ByteBuf的修改对原始的ByteBuf是可见的
        System.out.println(sliced.toString(utf8));//Jetty in Actio
//copy方法会重新分配新的ByteBuf，对其的修改对原始的ByteBuf不可见。
        System.out.println(copy.toString(utf8));//Netty in Actio
        System.out.println(buf1.getByte(0)==sliced.getByte(0));//true
        System.out.println(buf1.getByte(0)==copy.getByte(0));//false
    }
}
