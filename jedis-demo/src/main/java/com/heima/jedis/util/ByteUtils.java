package com.heima.jedis.util;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Some handy methods for dealing with {@code byte} arrays.
 *
 * @author Christoph Strobl
 * @author Mark Paluch
 * @since 1.7
 */
public final class ByteUtils {

	private ByteUtils() {}

	/**
	 * 将给定的 {@code byte} 数组连接成一个数组，并包含两次重叠的数组元素
	 * Concatenate the given {@code byte} arrays into one, with overlapping array elements included twice.
	 * <p />
	 * 保留原始数组中元素的顺序
	 * The order of elements in the original arrays is preserved.
	 *
	 * @param array1 the first array.
	 * @param array2 the second array.
	 * @return the new array.
	 */
	public static byte[] concat(byte[] array1, byte[] array2) {

		byte[] result = Arrays.copyOf(array1, array1.length + array2.length);
		System.arraycopy(array2, 0, result, array1.length, array2.length);

		return result;
	}

	/**
	 * 将给定的 {@code byte} 数组连接成一个数组，并包含两次重叠的数组元素。如果 {@code arrays} 为空，
	 * 则返回一个新的空数组，如果 {@code arrays} 仅包含单个数组，则返回第一个数组。
	 * Concatenate the given {@code byte} arrays into one, with overlapping array elements included twice. Returns a new,
	 * empty array if {@code arrays} was empty and returns the first array if {@code arrays} contains only a single array.
	 * <p />
	 * The order of elements in the original arrays is preserved.
	 *
	 * @param arrays the arrays.
	 * @return the new array.
	 */
	public static byte[] concatAll(byte[]... arrays) {

		if (arrays.length == 0) {
			return new byte[] {};
		}
		if (arrays.length == 1) {
			return arrays[0];
		}

		byte[] cur = concat(arrays[0], arrays[1]);
		for (int i = 2; i < arrays.length; i++) {
			cur = concat(cur, arrays[i]);
		}
		return cur;
	}

	/**
	 * 使用分隔符 {@code@code c} 将 { source} 拆分为分区数组。
	 * Split {@code source} into partitioned arrays using delimiter {@code c}.
	 *
	 * @param source the source array.
	 * @param c delimiter.
	 * @return the partitioned arrays.
	 */
	public static byte[][] split(byte[] source, int c) {

		if (ObjectUtils.isEmpty(source)) {
			return new byte[][] {};
		}

		List<byte[]> bytes = new ArrayList<>();
		int offset = 0;
		for (int i = 0; i <= source.length; i++) {

			if (i == source.length) {

				bytes.add(Arrays.copyOfRange(source, offset, i));
				break;
			}

			if (source[i] == c) {
				bytes.add(Arrays.copyOfRange(source, offset, i));
				offset = i + 1;
			}
		}
		return bytes.toArray(new byte[bytes.size()][]);
	}

	/**
	 * 将多个 {@code byte} 数组合并为一个数组
	 * Merge multiple {@code byte} arrays into one array
	 *
	 * @param firstArray must not be {@literal null}
	 * @param additionalArrays must not be {@literal null}
	 * @return
	 */
	public static byte[][] mergeArrays(byte[] firstArray, byte[]... additionalArrays) {

		Assert.notNull(firstArray, "first array must not be null");
		Assert.notNull(additionalArrays, "additional arrays must not be null");

		byte[][] result = new byte[additionalArrays.length + 1][];
		result[0] = firstArray;
		System.arraycopy(additionalArrays, 0, result, 1, additionalArrays.length);

		return result;
	}

	/**
	 * 从 {@link ByteBuffer} 中提取字节数组，而不使用它。
	 * Extract a byte array from {@link ByteBuffer} without consuming it.
	 *
	 * @param byteBuffer must not be {@literal null}.
	 * @return
	 * @since 2.0
	 */
	public static byte[] getBytes(ByteBuffer byteBuffer) {

		Assert.notNull(byteBuffer, "ByteBuffer must not be null!");

		ByteBuffer duplicate = byteBuffer.duplicate();
		byte[] bytes = new byte[duplicate.remaining()];
		duplicate.get(bytes);
		return bytes;
	}

	/**
	 * 测试 {@code haystack} 是否以给定的 {@code 前缀}开头。
	 * Tests if the {@code haystack} starts with the given {@code prefix}.
	 *
	 * @param haystack the source to scan.
	 * @param prefix the prefix to find.
	 * @return {@literal true} if {@code haystack} at position {@code offset} starts with {@code prefix}.
	 * @since 1.8.10
	 * @see #startsWith(byte[], byte[], int)
	 */
	public static boolean startsWith(byte[] haystack, byte[] prefix) {
		return startsWith(haystack, prefix, 0);
	}

	/**
	 * 测试从指定的 {@code offset} 开始的 {@code haystack} 是否以给定的 {@code 前缀}开头。
	 * Tests if the {@code haystack} beginning at the specified {@code offset} starts with the given {@code prefix}.
	 *
	 * @param haystack the source to scan.
	 * @param prefix the prefix to find.
	 * @param offset the offset to start at.
	 * @return {@literal true} if {@code haystack} at position {@code offset} starts with {@code prefix}.
	 * @since 1.8.10
	 */
	public static boolean startsWith(byte[] haystack, byte[] prefix, int offset) {

		int to = offset;
		int prefixOffset = 0;
		int prefixLength = prefix.length;

		if ((offset < 0) || (offset > haystack.length - prefixLength)) {
			return false;
		}

		while (--prefixLength >= 0) {
			if (haystack[to++] != prefix[prefixOffset++]) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 在指定的字节数组中搜索指定的值。返回
	 * Searches the specified array of bytes for the specified value. Returns the index of the first matching value in the
	 * {@code haystack}s natural order or {@code -1} of {@code needle} could not be found.
	 *
	 * @param haystack the source to scan.
	 * @param needle the value to scan for.
	 * @return index of first appearance, or -1 if not found.
	 * @since 1.8.10
	 */
	public static int indexOf(byte[] haystack, byte needle) {

		for (int i = 0; i < haystack.length; i++) {
			if (haystack[i] == needle) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * 使用 {@link java.nio.charset.StandardCharsets#UTF_8} 将 {@link String} 转换为 {@link ByteBuffer}。
	 * Convert a {@link String} into a {@link ByteBuffer} using {@link java.nio.charset.StandardCharsets#UTF_8}.
	 *
	 * @param theString must not be {@literal null}.
	 * @return
	 * @since 2.1
	 */
	public static ByteBuffer getByteBuffer(String theString) {
		return getByteBuffer(theString, StandardCharsets.UTF_8);
	}

	/**
	 * 使用给定的 {@link Charset} 将 {@link String} 转换为 {@link ByteBuffer}。
	 * Convert a {@link String} into a {@link ByteBuffer} using the given {@link Charset}.
	 *
	 * @param theString must not be {@literal null}.
	 * @param charset must not be {@literal null}.
	 * @return
	 * @since 2.1
	 */
	public static ByteBuffer getByteBuffer(String theString, Charset charset) {

		Assert.notNull(theString, "The String must not be null!");
		Assert.notNull(charset, "The String must not be null!");

		return charset.encode(theString);
	}

	/**
	 * 将给定的 {@link ByteBuffer} 转换为 {@link String}。
	 * Extract/Transfer bytes from the given {@link ByteBuffer} into an array by duplicating the buffer and fetching its
	 * content.
	 *
	 * @param buffer must not be {@literal null}.
	 * @return the extracted bytes.
	 * @since 2.1
	 */
	public static byte[] extractBytes(ByteBuffer buffer) {

		ByteBuffer duplicate = buffer.duplicate();
		byte[] bytes = new byte[duplicate.remaining()];
		duplicate.get(bytes);

		return bytes;
	}
}
