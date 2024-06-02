package com.mmodding.gradle.api.mod.json;

import org.gradle.api.Action;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.parsers.json.JsonWriter;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public interface CustomElement extends Serializable {

	void writeJson(JsonWriter writer) throws IOException;

	record CustomString(String value) implements CustomElement {

		@Override
		public void writeJson(JsonWriter writer) throws IOException {
			writer.value(this.value);
		}
	}

	record CustomBoolean(boolean value) implements CustomElement {

		@Override
		public void writeJson(JsonWriter writer) throws IOException {
			writer.value(this.value);
		}
	}

	record CustomNumber(Number value) implements CustomElement {

		@Override
		public void writeJson(JsonWriter writer) throws IOException {
			writer.value(this.value);
		}
	}

	record CustomArray(List<CustomElement> values) implements CustomElement {

		public void add(CustomElement value) {
			this.values.add(value);
		}

		public void add(String value) {
			this.add(new CustomString(value));
		}

		public void addUnique(String value) {
			var actual = new CustomString(value);
			if (!this.values.contains(actual)) {
				this.add(actual);
			}
		}

		public void add(boolean value) {
			this.add(new CustomBoolean(value));
		}

		public void add(Number number) {
			this.add(new CustomNumber(number));
		}

		public void addArray(Action<CustomArray> action) {
			var array = new CustomArray(new ArrayList<>());
			action.execute(array);
			this.add(array);
		}

		public void addBlock(Action<CustomBlock> action) {
			var block = new CustomBlock();
			action.execute(block);
			this.add(block);
		}

		@Override
		public void writeJson(JsonWriter writer) throws IOException {
			writer.beginArray();
			for (CustomElement element : this.values) {
				element.writeJson(writer);
			}
			writer.endArray();
		}
	}

	class CustomBlock implements CustomElement {

		private final Map<String, CustomElement> properties = new HashMap<>();

		public void put(String name, CustomElement element) {
			this.properties.put(name, element);
		}

		public void put(String name, String value) {
			this.put(name, new CustomString(value));
		}

		public void put(String name, boolean value) {
			this.put(name, new CustomBoolean(value));
		}

		public void put(String name, Number number) {
			this.put(name, new CustomNumber(number));
		}

		public void putArray(String name, Action<CustomArray> action) {
			var array = new CustomArray(new ArrayList<>());
			action.execute(array);
			this.put(name, array);
		}

		public void withArray(String name, Action<CustomArray> action) {
			var value = this.properties.computeIfAbsent(name, ignored -> new CustomArray(new ArrayList<>()));

			if (value instanceof CustomArray array) {
				action.execute(array);
			} else {
				throw new IllegalArgumentException("An element is already present at key \"" + name + "\" with value " + value + ".");
			}
		}

		public void putBlock(String name, Action<CustomBlock> action) {
			var block = new CustomBlock();
			action.execute(block);
			this.put(name, block);
		}

		public void withBlock(String name, Action<CustomBlock> action) {
			var value = this.properties.computeIfAbsent(name, ignored -> new CustomBlock());

			if (value instanceof CustomBlock block) {
				action.execute(block);
			} else {
				throw new IllegalArgumentException("An element is already present at key \"" + name + "\" with value " + value + ".");
			}
		}

		public boolean isEmpty() {
			return this.properties.isEmpty();
		}

		public void forEach(@NotNull CustomElementConsumer consumer) throws IOException {
			Objects.requireNonNull(consumer);
			for (Map.Entry<String, CustomElement> entry : this.properties.entrySet()) {
				String string;
				CustomElement element;
				try {
					string = entry.getKey();
					element = entry.getValue();
				} catch (IllegalStateException ise) {
					throw new ConcurrentModificationException(ise);
				}
				consumer.accept(string, element);
			}
		}

		public void writeJson(JsonWriter writer) throws IOException {
			writer.beginObject();
			for (Map.Entry<String, CustomElement> element : this.properties.entrySet()) {
				writer.name(element.getKey());
				element.getValue().writeJson(writer);
			}
			writer.endObject();
		}

		public interface CustomElementConsumer {

			void accept(String string, CustomElement element) throws IOException;
		}
	}
}
