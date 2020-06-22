package quarris.rotm.config.utils;

import java.util.Arrays;
import java.util.function.*;

public class StringConfig {

    private static final Function<String, String> DEFAULT_CONVERTER = Function.identity();

    private final Function<Integer, StringConfigException> outOfArgsException;
    private final Function<Integer, StringConfigException> converterException;
    private final Function<Integer, StringConfigException> invalidArgumentException;
    private final StringConfigException parseRestException;

    private final String[] args;
    private int argIndex = -1;
    private Function<String, ?> converter = DEFAULT_CONVERTER;

    private boolean isOptional;
    private boolean parseRemaining;
    private Object defaultOptional;

    public StringConfig(String args) {
        this.args = args.split(";");

        this.outOfArgsException = (index) -> new StringConfigException("Tried to get argument at [" + index + "] which does not exist in '" + Arrays.toString(this.args) + "'");
        this.converterException = (index) -> new StringConfigException("Expected a different type of value at [" + index + "] but instead got '" + this.args[index] + "'");
        this.invalidArgumentException = (index) -> new StringConfigException("Could not validate argument '" + this.args[index] + "' at [" + index + "]");
        this.parseRestException = new StringConfigException("Attempted to parse next after parsing remaining");
    }

    public final StringConfig next() throws StringConfigException {
        if (this.parseRemaining) {
            throw this.parseRestException;
        }
        this.argIndex++;
        this.isOptional = false;
        this.converter = DEFAULT_CONVERTER;
        return this;
    }

    public final StringConfig rest() throws StringConfigException {
        this.next();
        this.parseRemaining = true;
        return this;
    }

    public final <T> StringConfig parseAs(Function<String, T> converter) {
        this.converter = converter;
        return this;
    }

    public final <T> StringConfig optional(T def) {
        this.isOptional = true;
        this.defaultOptional = def;
        return this;
    }

    public final <T> StringConfig validate(Predicate<T> predicate) throws StringConfigException {
        if (this.parseRemaining) {
            for (int i = this.argIndex; i < this.args.length; i++) {
                if (!predicate.test(this.getArg(i))) {
                    throw invalidArgumentException.apply(i);
                }
            }
            return this;
        } else if (predicate.test(this.getArg())) {
            return this;
        }

        throw invalidArgumentException.apply(this.argIndex);
    }

    public final <T> StringConfig validateRange(BiPredicate<T, T> predicate) throws StringConfigException {
        String[] split;
        try {
            split = this.args[this.argIndex].split("-", 2);
        } catch (ArrayIndexOutOfBoundsException e) {
            if (!this.isOptional) {
                throw this.outOfArgsException.apply(this.argIndex);
            }
            return this;
        }
        if (split.length == 1) {
            split = new String[]{split[0], split[0]};
        }
        try {
            if (predicate.test((T) this.converter.apply(split[0]), (T) this.converter.apply(split[1]))) {
                return this;
            }
        } catch (Exception ignored) {
            throw this.converterException.apply(this.argIndex);
        }
        throw this.invalidArgumentException.apply(this.argIndex);
    }

    public final <T> StringConfig accept(Consumer<T> action) throws StringConfigException {
        if (this.parseRemaining) {
            for (int i = this.argIndex; i < this.args.length; i++) {
                action.accept(this.getArg(i));
            }
        } else {
            action.accept(this.getArg());
        }
        return this;
    }

    public final <T> StringConfig acceptRange(Consumer<T> firstAction, Consumer<T> secondAction) throws StringConfigException {
        String[] split;
        try {
            split = this.args[this.argIndex].split("-", 2);
        } catch (ArrayIndexOutOfBoundsException e) {
            if (!this.isOptional) {
                throw this.outOfArgsException.apply(this.argIndex);
            } else {
                split = (String[]) this.defaultOptional;
            }
        }
        if (split.length == 1) {
            split = new String[]{split[0], split[0]};
        }
        try {
            firstAction.accept((T) this.converter.apply(split[0]));
            secondAction.accept((T) this.converter.apply(split[1]));
        } catch (ClassCastException e) {
            throw this.converterException.apply(this.argIndex);
        }
        return this;
    }

    private final <T> T getArg() throws StringConfigException {
        return this.getArg(this.argIndex);
    }

    private final <T> T getArg(int index) throws StringConfigException {
        T value;
        try {
            value = (T) converter.apply(this.args[index]);
        } catch (ArrayIndexOutOfBoundsException | ClassCastException e) {
            if (this.isOptional) {
                value = (T) this.defaultOptional;
            } else {
                throw this.converterException.apply(this.argIndex);
            }
        }
        return value;
    }
}
