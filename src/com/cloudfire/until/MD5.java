package com.cloudfire.until;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sun.misc.BASE64Encoder;

/*************************************************
md5 閿熸枻鎷峰疄閿熸枻鎷烽敓鏂ゆ嫹RSA Data Security, Inc.閿熸枻鎷烽敓缁撲氦閿熸枻鎷稩ETF
閿熸枻鎷稲FC1321閿熷彨纰夋嫹MD5 message-digest 閿熷娉曢敓鏂ゆ嫹
*************************************************/
public class MD5 {
  /* 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷蜂簺S11-S44瀹為敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹涓�閿熸枻鎷�4*4閿熶茎鎾呮嫹閿熸枻鎷烽敓鏂ゆ嫹鍘熷閿熸枻鎷稢瀹為敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷�#define 瀹為敓琛楃殑锝忔嫹
  閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熺粸纰夋嫹娈栭敓杞跨尰tatic final閿熻鎲嬫嫹绀洪敓鏂ゆ嫹鍙敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹鍚屼竴閿熸枻鎷烽敓鏂ゆ嫹鐐崰閿熸枻鎷疯瘶浜╅敓鏂ゆ嫹
  Instance閿熸垝鍏遍敓鏂ゆ嫹*/
    static final int S11 = 7;
    static final int S12 = 12;
    static final int S13 = 17;
    static final int S14 = 22;

    static final int S21 = 5;
    static final int S22 = 9;
    static final int S23 = 14;
    static final int S24 = 20;

    static final int S31 = 4;
    static final int S32 = 11;
    static final int S33 = 16;
    static final int S34 = 23;

    static final int S41 = 6;
    static final int S42 = 10;
    static final int S43 = 15;
    static final int S44 = 21;

    static final byte[] PADDING = { -128, 0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    /* 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷峰憳閿熸枻鎷稭D5閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熺煫纰夋嫹閿熸枻鎷�3閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹鑽╅敓鏂ゆ嫹閿熺殕顓冪》鎷烽敓绱哄疄閿熸枻鎷烽敓鏂ゆ嫹
      閿熸枻鎷烽敓鏂ゆ嫹閿熻棄鍒癕D5_CTX閿熺粨鏋勯敓鏂ゆ嫹
   
      */
    private long[] state = new long[4]; // state (ABCD)
    private long[] count = new long[2]; // number of bits, modulo 2^64 (lsb first)
    private byte[] buffer = new byte[64]; // input buffer
   
  /* digestHexStr閿熸枻鎷稭D5閿熸枻鎷峰敮涓�涓�閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹鍛橀敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷蜂竴閿熻娇纭锋嫹閿熸枻鎷烽敓鏂ゆ嫹閿燂拷
  閿熸枻鎷� 16閿熸枻鎷烽敓鏂ゆ嫹ASCII閿熸枻鎷风ず.
  */
    public String digestHexStr;
   
    /* digest,閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷蜂竴閿熻娇纭锋嫹閿熸枻鎷烽敓鏂ゆ嫹閿燂拷閿熸枻鎷烽敓鏂ゆ嫹閿熻妭璇ф嫹閿熸枻鎷风ず閿熸枻鎷烽敓鏂ゆ嫹绀�128bit閿熸枻鎷稭D5鍊�.
  */
    private byte[] digest = new byte[16];
   
  /*
    getMD5ofStr閿熸枻鎷烽敓鏂ゆ嫹MD5閿熸枻鎷烽敓鏂ゆ嫹瑕侀敓渚ョ櫢鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹璇撻敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鎻亷鎷烽敓鏂ゆ嫹閿熺祫D5閿熸垝鎹㈤敓鏂ゆ嫹閿熻鍑ゆ嫹
    閿熸枻鎷烽敓鎴鎷烽敓瑙掑彉鎹㈤敓鏂ゆ嫹鎱曢敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓瑙掍粠鐧告嫹閿熸枻鎷烽敓鏂ゆ嫹鍛榙igestHexStr鍙栭敓鐭殑锝忔嫹
  */
    public String getMD5ofStr(String inbuf) {
          md5Init();
          md5Update(inbuf.getBytes(), inbuf.length());
          md5Final();
          digestHexStr = "";
          for (int i = 0; i < 16; i++) {
                digestHexStr += byteHEX(digest[i]);
          }
          return digestHexStr;

    }
    // 閿熸枻鎷烽敓鏂ゆ嫹MD5閿熸枻鎷烽敓鏂ゆ嫹閿熶茎鎲嬫嫹鍑嗛敓鏂ゆ嫹閿熷眾鍑介敓鏂ゆ嫹JavaBean瑕侀敓鏂ゆ嫹閿熸枻鎷蜂竴閿熸枻鎷穚ublic閿熶茎璇ф嫹閿熸枻鎷锋病閿熷彨璇ф嫹閿熸枻鎷峰閿熸枻鎷锋棇顖ゆ嫹閿燂拷
    public MD5() {
          md5Init();

          return;
    }
    /* md5Init閿熸枻鎷蜂竴閿熸枻鎷烽敓鏂ゆ嫹濮嬮敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熺粸纭锋嫹閿熸枻鎷烽敓鏂ゆ嫹璋嬮敓鏂ゆ嫹閿熸枻鎷烽敓闃跺府鎷烽敓鏂ゆ嫹鍑嗛敓渚ヤ紮鎷烽敓鏂ゆ嫹 */
    private void md5Init() {
          count[0] = 0L;
          count[1] = 0L;
          ///* Load magic initialization constants.

          state[0] = 0x67452301L;
          state[1] = 0xefcdab89L;
          state[2] = 0x98badcfeL;
          state[3] = 0x10325476L;

          return;
    }
    /* F, G, H ,I 閿熸枻鎷�4閿熸枻鎷烽敓鏂ゆ嫹閿熺祫D5閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷峰師濮嬮敓鏂ゆ嫹MD5閿熸枻鎷稢瀹為敓鏂ゆ嫹閿熷彨锝忔嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷�
    閿熸触鍗曠鎷蜂綅閿熸枻鎷烽敓濮愶紝閿熸枻鎷烽敓鏉扮鎷烽敓鏂ゆ嫹鏁堥敓缁炵殑鍖℃嫹閿熻甯嫹閿熸枻鎷烽敓鏂ゆ嫹瀹為敓琛楃鎷烽敓鍓垮畯锛岄敓鏂ゆ嫹java閿熷彨锝忔嫹閿熸枻鎷烽敓瑙掑府鎷烽敓鏂ゆ嫹閿熸枻鎷�
  閿熸枻鎷烽敓鏂ゆ嫹瀹為敓琛楃鎷烽敓鏂ゆ嫹private閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熻鎲嬫嫹閿熸枻鎷烽敓鏂ゆ嫹鍘熼敓鏂ゆ嫹C閿熷彨鐨勨槄鎷� */

    private long F(long x, long y, long z) {
          return (x & y) | ((~x) & z);

    }
    private long G(long x, long y, long z) {
          return (x & z) | (y & (~z));

    }
    private long H(long x, long y, long z) {
          return x ^ y ^ z;
    }

    private long I(long x, long y, long z) {
          return y ^ (x | (~z));
    }
   
    /*
      FF,GG,HH閿熸枻鎷稩I閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷稦,G,H,I閿熸枻鎷烽敓鍙枻鎷蜂竴閿熸枻鎷烽敓鎴掓崲
      FF, GG, HH, and II transformations for rounds 1, 2, 3, and 4.
      Rotation is separate from addition to prevent recomputation.
    */

    private long FF(long a, long b, long c, long d, long x, long s,
          long ac) {
          a += F (b, c, d) + x + ac;
          a = ((int) a << s) | ((int) a >>> (32 - s));
          a += b;
          return a;
    }

    private long GG(long a, long b, long c, long d, long x, long s,
          long ac) {
          a += G (b, c, d) + x + ac;
          a = ((int) a << s) | ((int) a >>> (32 - s));
          a += b;
          return a;
    }
    private long HH(long a, long b, long c, long d, long x, long s,
          long ac) {
          a += H (b, c, d) + x + ac;
          a = ((int) a << s) | ((int) a >>> (32 - s));
          a += b;
          return a;
    }
    private long II(long a, long b, long c, long d, long x, long s,
          long ac) {
          a += I (b, c, d) + x + ac;
          a = ((int) a << s) | ((int) a >>> (32 - s));
          a += b;
          return a;
    }
    /*
      md5Update閿熸枻鎷稭D5閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸暀锝忔嫹inbuf閿熸枻鎷疯閿熸垝鎹㈤敓鏂ゆ嫹閿熻鑺傝揪鎷烽敓鏂ゆ嫹inputlen閿熻绛规嫹閿熼ズ锝忔嫹閿熸枻鎷烽敓锟�
      閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷穏etMD5ofStr閿熸枻鎷烽敓鐭綇鎷烽敓鏂ゆ嫹閿熸枻鎷蜂箣鍓嶉敓鏂ゆ嫹瑕侀敓鏂ゆ嫹閿熸枻鎷穖d5init閿熸枻鎷烽敓鏂ゆ嫹绋庨敓鏂ゆ嫹閿熸枻鎷烽敓鐙＄鎷穚rivate閿熸枻鎷�
    */
    private void md5Update(byte[] inbuf, int inputLen) {

          int i, index, partLen;
          byte[] block = new byte[64];
          index = (int)(count[0] >>> 3) & 0x3F;
          // /* Update number of bits */
          if ((count[0] += (inputLen << 3)) < (inputLen << 3))
                count[1]++;
          count[1] += (inputLen >>> 29);

          partLen = 64 - index;

          // Transform as many times as possible.
          if (inputLen >= partLen) {
                md5Memcpy(buffer, inbuf, index, 0, partLen);
                md5Transform(buffer);

                for (i = partLen; i + 63 < inputLen; i += 64) {

                    md5Memcpy(block, inbuf, 0, i, 64);
                    md5Transform (block);
                }
                index = 0;

          } else

                i = 0;

          ///* Buffer remaining input */
          md5Memcpy(buffer, inbuf, index, i, inputLen - i);

    }
   
    /*
      md5Final閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鍙揪鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓锟�
    */
    private void md5Final () {
          byte[] bits = new byte[8];
          int index, padLen;

          ///* Save number of bits */
          Encode (bits, count, 8);

          ///* Pad out to 56 mod 64.
          index = (int)(count[0] >>> 3) & 0x3f;
          padLen = (index < 56) ? (56 - index) : (120 - index);
          md5Update (PADDING, padLen);

          ///* Append length (before padding) */
          md5Update(bits, 8);

          ///* Store state in digest */
          Encode (digest, state, 16);

    }
     
    /* md5Memcpy閿熸枻鎷蜂竴閿熸枻鎷烽敓鑺傝鎷蜂娇閿熺煫纰夋嫹byte閿熸枻鎷烽敓鏂ゆ嫹鐩Ν鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷穒nput閿熸枻鎷穒npos閿熸枻鎷峰閿熸枻鎷穕en閿熸枻鎷烽敓楗虹鎷�
閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷� 閿熻鑺傚尅鎷烽敓鏂ゆ嫹閿熸枻鎷穙utput閿熸枻鎷穙utpos浣嶉敓鐭尅鎷峰
    */

    private void md5Memcpy (byte[] output, byte[] input,
          int outpos, int inpos, int len)
    {
          int i;

          for (i = 0; i < len; i++)
                output[outpos + i] = input[inpos + i];
    }
   
    /*
      md5Transform閿熸枻鎷稭D5閿熸枻鎷烽敓渚ュ彉鎹㈤敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹md5Update閿熸枻鎷烽敓鐭綇鎷穊lock閿熻鍒嗗尅鎷烽敓鐨嗩厓纭锋嫹绾搁敓锟�
    */
    private void md5Transform (byte block[]) {
          long a = state[0], b = state[1], c = state[2], d = state[3];
          long[] x = new long[16];

          Decode (x, block, 64);

          /* Round 1 */
          a = FF (a, b, c, d, x[0], S11, 0xd76aa478L); /* 1 */
          d = FF (d, a, b, c, x[1], S12, 0xe8c7b756L); /* 2 */
          c = FF (c, d, a, b, x[2], S13, 0x242070dbL); /* 3 */
          b = FF (b, c, d, a, x[3], S14, 0xc1bdceeeL); /* 4 */
          a = FF (a, b, c, d, x[4], S11, 0xf57c0fafL); /* 5 */
          d = FF (d, a, b, c, x[5], S12, 0x4787c62aL); /* 6 */
          c = FF (c, d, a, b, x[6], S13, 0xa8304613L); /* 7 */
          b = FF (b, c, d, a, x[7], S14, 0xfd469501L); /* 8 */
          a = FF (a, b, c, d, x[8], S11, 0x698098d8L); /* 9 */
          d = FF (d, a, b, c, x[9], S12, 0x8b44f7afL); /* 10 */
          c = FF (c, d, a, b, x[10], S13, 0xffff5bb1L); /* 11 */
          b = FF (b, c, d, a, x[11], S14, 0x895cd7beL); /* 12 */
          a = FF (a, b, c, d, x[12], S11, 0x6b901122L); /* 13 */
          d = FF (d, a, b, c, x[13], S12, 0xfd987193L); /* 14 */
          c = FF (c, d, a, b, x[14], S13, 0xa679438eL); /* 15 */
          b = FF (b, c, d, a, x[15], S14, 0x49b40821L); /* 16 */

          /* Round 2 */
          a = GG (a, b, c, d, x[1], S21, 0xf61e2562L); /* 17 */
          d = GG (d, a, b, c, x[6], S22, 0xc040b340L); /* 18 */
          c = GG (c, d, a, b, x[11], S23, 0x265e5a51L); /* 19 */
          b = GG (b, c, d, a, x[0], S24, 0xe9b6c7aaL); /* 20 */
          a = GG (a, b, c, d, x[5], S21, 0xd62f105dL); /* 21 */
          d = GG (d, a, b, c, x[10], S22, 0x2441453L); /* 22 */
          c = GG (c, d, a, b, x[15], S23, 0xd8a1e681L); /* 23 */
          b = GG (b, c, d, a, x[4], S24, 0xe7d3fbc8L); /* 24 */
          a = GG (a, b, c, d, x[9], S21, 0x21e1cde6L); /* 25 */
          d = GG (d, a, b, c, x[14], S22, 0xc33707d6L); /* 26 */
          c = GG (c, d, a, b, x[3], S23, 0xf4d50d87L); /* 27 */
          b = GG (b, c, d, a, x[8], S24, 0x455a14edL); /* 28 */
          a = GG (a, b, c, d, x[13], S21, 0xa9e3e905L); /* 29 */
          d = GG (d, a, b, c, x[2], S22, 0xfcefa3f8L); /* 30 */
          c = GG (c, d, a, b, x[7], S23, 0x676f02d9L); /* 31 */
          b = GG (b, c, d, a, x[12], S24, 0x8d2a4c8aL); /* 32 */

          /* Round 3 */
          a = HH (a, b, c, d, x[5], S31, 0xfffa3942L); /* 33 */
          d = HH (d, a, b, c, x[8], S32, 0x8771f681L); /* 34 */
          c = HH (c, d, a, b, x[11], S33, 0x6d9d6122L); /* 35 */
          b = HH (b, c, d, a, x[14], S34, 0xfde5380cL); /* 36 */
          a = HH (a, b, c, d, x[1], S31, 0xa4beea44L); /* 37 */
          d = HH (d, a, b, c, x[4], S32, 0x4bdecfa9L); /* 38 */
          c = HH (c, d, a, b, x[7], S33, 0xf6bb4b60L); /* 39 */
          b = HH (b, c, d, a, x[10], S34, 0xbebfbc70L); /* 40 */
          a = HH (a, b, c, d, x[13], S31, 0x289b7ec6L); /* 41 */
          d = HH (d, a, b, c, x[0], S32, 0xeaa127faL); /* 42 */
          c = HH (c, d, a, b, x[3], S33, 0xd4ef3085L); /* 43 */
          b = HH (b, c, d, a, x[6], S34, 0x4881d05L); /* 44 */
          a = HH (a, b, c, d, x[9], S31, 0xd9d4d039L); /* 45 */
          d = HH (d, a, b, c, x[12], S32, 0xe6db99e5L); /* 46 */
          c = HH (c, d, a, b, x[15], S33, 0x1fa27cf8L); /* 47 */
          b = HH (b, c, d, a, x[2], S34, 0xc4ac5665L); /* 48 */

          /* Round 4 */
          a = II (a, b, c, d, x[0], S41, 0xf4292244L); /* 49 */
          d = II (d, a, b, c, x[7], S42, 0x432aff97L); /* 50 */
          c = II (c, d, a, b, x[14], S43, 0xab9423a7L); /* 51 */
          b = II (b, c, d, a, x[5], S44, 0xfc93a039L); /* 52 */
          a = II (a, b, c, d, x[12], S41, 0x655b59c3L); /* 53 */
          d = II (d, a, b, c, x[3], S42, 0x8f0ccc92L); /* 54 */
          c = II (c, d, a, b, x[10], S43, 0xffeff47dL); /* 55 */
          b = II (b, c, d, a, x[1], S44, 0x85845dd1L); /* 56 */
          a = II (a, b, c, d, x[8], S41, 0x6fa87e4fL); /* 57 */
          d = II (d, a, b, c, x[15], S42, 0xfe2ce6e0L); /* 58 */
          c = II (c, d, a, b, x[6], S43, 0xa3014314L); /* 59 */
          b = II (b, c, d, a, x[13], S44, 0x4e0811a1L); /* 60 */
          a = II (a, b, c, d, x[4], S41, 0xf7537e82L); /* 61 */
          d = II (d, a, b, c, x[11], S42, 0xbd3af235L); /* 62 */
          c = II (c, d, a, b, x[2], S43, 0x2ad7d2bbL); /* 63 */
          b = II (b, c, d, a, x[9], S44, 0xeb86d391L); /* 64 */

          state[0] += a;
          state[1] += b;
          state[2] += c;
          state[3] += d;

    }
   
    /*Encode閿熸枻鎷穕ong閿熸枻鎷烽敓浠嬫寜椤洪敓鏂ゆ嫹閿熸枻鎷穊yte閿熸枻鎷烽敓浠嬶紝閿熸枻鎷蜂负java閿熸枻鎷穕ong閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷�64bit閿熶茎锝忔嫹
      鍙敓鏂ゆ嫹閿燂拷2bit閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷峰簲鍘熷C瀹為敓琛楃鎷烽敓鏂ゆ嫹閫�
    */
    private void Encode (byte[] output, long[] input, int len) {
          int i, j;

          for (i = 0, j = 0; j < len; i++, j += 4) {
                output[j] = (byte)(input[i] & 0xffL);
                output[j + 1] = (byte)((input[i] >>> 8) & 0xffL);
                output[j + 2] = (byte)((input[i] >>> 16) & 0xffL);
                output[j + 3] = (byte)((input[i] >>> 24) & 0xffL);
          }
    }

    /*Decode閿熸枻鎷穊yte閿熸枻鎷烽敓浠嬫寜椤洪敓鏂ゆ嫹閾ｆ矙閿熺担ong閿熸枻鎷烽敓浠嬶紝閿熸枻鎷蜂负java閿熸枻鎷穕ong閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷�64bit閿熶茎锝忔嫹
      鍙敓杈冩垚纰夋嫹32bit閿熸枻鎷烽敓鏂ゆ嫹32bit閿熸枻鎷烽敓濮愶紝閿熸枻鎷烽敓鏂ゆ嫹搴斿師濮婥瀹為敓琛楃鎷烽敓鏂ゆ嫹閫�
    */
    private void Decode (long[] output, byte[] input, int len) {
          int i, j;


          for (i = 0, j = 0; j < len; i++, j += 4)
                output[i] = b2iu(input[j]) |
                    (b2iu(input[j + 1]) << 8) |
                    (b2iu(input[j + 2]) << 16) |
                    (b2iu(input[j + 3]) << 24);

          return;
    }
   
    /*
      b2iu閿熸枻鎷烽敓鏂ゆ嫹鍐欓敓鏂ゆ嫹涓�閿熸枻鎷烽敓鏂ゆ嫹byte閿熸枻鎷烽敓绉歌鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹璇洪敓鐨嗩叏鎷烽敓渚ワ綇鎷烽敓鏂ゆ嫹浣嶉敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷蜂负java娌￠敓鏂ゆ嫹unsigned閿熸枻鎷烽敓鏂ゆ嫹
    */
    public static long b2iu(byte b) {
          return b < 0 ? b & 0x7F + 128 : b;
    }
   
  /*byteHEX()閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹涓�閿熸枻鎷穊yte閿熸枻鎷烽敓閰电鎷烽敓鏂ゆ嫹杞敓鏂ゆ嫹閿熸枻鎷峰崄閿熸枻鎷烽敓鏂ゆ嫹棰戦敓绱窼CII閿熸枻鎷风ず閿熸枻鎷�
  閿熸枻鎷烽敓鏂ゆ嫹涓簀ava閿熷彨纰夋嫹byte閿熸枻鎷穞oString閿熺潾鍑ゆ嫹瀹為敓鏂ゆ嫹閿熸枻鎷蜂竴閿熷锛岄敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹娌￠敓鏂ゆ嫹C閿熸枻鎷烽敓鏂ゆ嫹閿熷彨纰夋嫹
    sprintf(outbuf,"%02X",ib)
  */
    public static String byteHEX(byte ib) {
          char[] Digit = { '0','1','2','3','4','5','6','7','8','9',
          'A','B','C','D','E','F' };
          char [] ob = new char[2];
          ob[0] = Digit[(ib >>> 4) & 0X0F];
          ob[1] = Digit[ib & 0X0F];
          String s = new String(ob);
          return s;
    }
    
  //随机生成6个字符串  （盐）
    public static String rundomStr() {
        String result = "";
        for (int i = 0; i < 6; i++) {
            int intVal = (int) (Math.random() * 26 + 97);
            result = result + (char) intVal;
        }
        return result;
    }
    
    

    public static String encrypt(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("md5"); // 消息摘要类
            byte[] bytes = digest.digest(str.getBytes()); // 对字符串明文进行加密，字节数组。返回加密好的字节数组(乱码)
            BASE64Encoder encoder = new BASE64Encoder(); // 基于Base64字符编码的类，可以把乱码转化成可读性较好的字符串
            return encoder.encode(bytes); // 把md5加密后所产生的乱码的字节数组转化成字符串
        } catch (NoSuchAlgorithmException e) { // 未找到的加密算法异常
            e.printStackTrace();
        }
        return null;
    }
    
    
}