package com.lapissea.util.function;

import java.util.Objects;

public interface FloatConsumer{
	
	void accept(float t);
	
	/**
	 * Returns a composed {@code Consumer} that performs, in sequence, this
	 * operation followed by the {@code after} operation. If performing either
	 * operation throws an exception, it is relayed to the caller of the
	 * composed operation.  If performing this operation throws an exception,
	 * the {@code after} operation will not be performed.
	 *
	 * @param after the operation to perform after this operation
	 * @return a composed {@code Consumer} that performs in sequence this
	 * operation followed by the {@code after} operation
	 * @throws NullPointerException if {@code after} is null
	 */
	default FloatConsumer andThen(FloatConsumer after){
		Objects.requireNonNull(after);
		return (t)->{ accept(t); after.accept(t); };
	}
}
