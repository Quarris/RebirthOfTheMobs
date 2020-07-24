package quarris.rotm.config.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class StringConfig {

    private static final Function<String, String> DEFAULT_CONVERTER = Function.identity();

    private final Function<Integer, StringConfigException> outOfArgsException;
    private final Function<Integer, StringConfigException> converterException;
    private final Function<Integer, StringConfigException> invalidArgumentException;
    private final StringConfigException parseRestException;

    private final String[] args;
    private int argIndex = -1;
    private Function<String, ?> converter = DEFAULT_CONVERTER;

    private boolean lastOptional;
    private boolean parseRemaining;
    private Object defaultOptional;
    private boolean lastOptionalSucceeded;

    public StringConfig(String args) {
        this.args = args.split(";");

        this.outOfArgsException = (index) -> new StringConfigException("Tried to get argument at [" + index + "] which does not exist in '" + Arrays.toString(this.args) + "'");
        this.converterException = (index) -> new StringConfigException("Expected a different type of value at [" + index + "] but instead got '" + (this.args.length >= index ? "null" : this.args[index]) + "'");
        this.invalidArgumentException = (index) -> new StringConfigException("Could not validate argument '" + this.args[index] + "' at [" + index + "]");
        this.parseRestException = new StringConfigException("Attempted to parse next after parsing remaining");
    }

    public final StringConfig next() throws StringConfigException {
        if (this.parseRemaining) {
            throw this.parseRestException;
        }
        if (!this.lastOptional || this.lastOptionalSucceeded) {
            this.argIndex++;
        }
        this.lastOptionalSucceeded = false;
        this.lastOptional = false;
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
        this.lastOptional = true;
        this.lastOptionalSucceeded = true;
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

        if (!this.lastOptional) {
            throw invalidArgumentException.apply(this.argIndex);
        }
        this.lastOptionalSucceeded = false;
        return this;
    }

    public final <T> StringConfig validateRange(BiPredicate<T, T> predicate) throws StringConfigException {
        String[] split;
        try {
            split = this.args[this.argIndex].split("-", 2);
        } catch (ArrayIndexOutOfBoundsException e) {
            if (!this.lastOptional) {
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

    public final <T> StringConfig validateList(Predicate<T> itemPredicate) throws StringConfigException {
        List<T> list = this.getList(this.argIndex);
        for (T item : list) {
            if (!itemPredicate.test(item)) {
                throw this.invalidArgumentException.apply(this.argIndex);
            }
        }
        return this;
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
            if (!this.lastOptional) {
                throw this.outOfArgsException.apply(this.argIndex);
            } else {
                this.lastOptionalSucceeded = false;
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

    private <T> List<T> getList(int index) throws StringConfigException {
        String raw = this.getRawList(index);
        List<T> list = new ArrayList<>();

        String innerRaw = raw.substring(raw.indexOf('[') + 1, raw.indexOf(']')).replace(" ", "");
        if (innerRaw.isEmpty()) {
            return list;
        }

        String[] split = innerRaw.split(",");
        for (String itemString : split) {
            try {
                list.add((T) this.converter.apply(itemString));
            } catch (ClassCastException e) {
                if (!this.lastOptional) {
                    throw this.converterException.apply(this.argIndex);
                }
                this.lastOptionalSucceeded = false;
                return Collections.emptyList();
            }
        }
        return list;
    }

    private <T> T getArg() throws StringConfigException {
        return this.getArg(this.argIndex);
    }

    private <T> T getArg(int index) throws StringConfigException {
        T value;
        try {
            value = (T) converter.apply(this.args[index]);
        } catch (Exception e) {
            if (this.lastOptional) {
                this.lastOptionalSucceeded = false;
                value = (T) this.defaultOptional;
            } else {
                throw this.converterException.apply(this.argIndex);
            }
        }
        return value;
    }

    public StringConfig blockList(Consumer<Boolean> blockList) throws StringConfigException {
        String raw = this.getRawList(this.argIndex);
        blockList.accept(raw.startsWith("!"));
        return this;
    }

    public String getRawList(int index) throws StringConfigException {
        String raw;
        try {
            raw = this.args[index];
        } catch (ArrayIndexOutOfBoundsException e) {
            if (!this.lastOptional) {
                throw this.outOfArgsException.apply(index);
            }
            raw = (String) this.defaultOptional;
            this.lastOptionalSucceeded = false;
        }

        if (!(raw.endsWith("]") && (raw.startsWith("[") || raw.startsWith("!") && raw.charAt(1) == '['))) {
            raw = (String) this.defaultOptional;
            this.lastOptionalSucceeded = false;
        }

        return raw;
    }

    public <T> StringConfig acceptList(Consumer<T> action) throws StringConfigException {
        List<T> list = this.getList(this.argIndex);
        for (T item : list)
            action.accept(item);

        return this;
    }
}
