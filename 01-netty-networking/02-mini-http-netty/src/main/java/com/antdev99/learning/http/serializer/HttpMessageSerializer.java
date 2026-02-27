package com.antdev99.learning.http.serializer;

import com.antdev99.learning.http.MiniHttpMessage;

/**
 * An interface for serializing a MiniHttpMessage into its string representation.
 * This is used to convert a MiniHttpMessage into a format that can be sent over the network.
 */
public interface HttpMessageSerializer<T extends MiniHttpMessage> {

    /**
     * Serializes a MiniHttpMessage into its string representation.
     *
     * @param message the MiniHttpMessage to serialize
     * @return the string representation of the MiniHttpMessage
     */
    String serialize(T message);
}
