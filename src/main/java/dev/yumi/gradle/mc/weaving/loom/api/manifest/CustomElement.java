package dev.yumi.gradle.mc.weaving.loom.api.manifest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.gradle.api.Action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public interface CustomElement<J extends JsonElement> extends Serializable {
	J toJson();

	record CustomString(String value) implements CustomElement<JsonPrimitive> {
		@Override
		public JsonPrimitive toJson() {
			return new JsonPrimitive(this.value);
		}
	}

	record CustomBoolean(boolean value) implements CustomElement<JsonPrimitive> {
		@Override
		public JsonPrimitive toJson() {
			return new JsonPrimitive(this.value);
		}
	}

	record CustomNumber(Number value) implements CustomElement<JsonPrimitive> {
		@Override
		public JsonPrimitive toJson() {
			return new JsonPrimitive(this.value);
		}
	}

	record CustomArray(List<CustomElement<?>> value) implements CustomElement<JsonArray> {
		public void add(CustomElement<?> value) {
			this.value.add(value);
		}

		public void add(String value) {
			this.add(new CustomString(value));
		}

		public void addUnique(String value) {
			var actual = new CustomString(value);
			if (!this.value.contains(actual)) {
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
		public JsonArray toJson() {
			var json = new JsonArray();
			this.value.forEach(item -> json.add(item.toJson()));
			return json;
		}
	}

	class CustomBlock implements CustomElement<JsonObject> {
		private final Map<String, CustomElement<?>> properties = new HashMap<>();

		public void put(String name, CustomElement<?> element) {
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
			var value = this.properties.computeIfAbsent(name, (ignored) -> new CustomArray(new ArrayList<>()));

			if (value instanceof CustomArray array) {
				action.execute(array);
			} else {
				throw new IllegalArgumentException(
						"An element is already present at key \"" + name + "\" with value " + value + "."
				);
			}
		}

		public void putBlock(String name, Action<CustomBlock> action) {
			var block = new CustomBlock();
			action.execute(block);
			this.put(name, block);
		}

		public void withBlock(String name, Action<CustomBlock> action) {
			var value = this.properties.computeIfAbsent(name, (ignored) -> new CustomBlock());

			if (value instanceof CustomBlock block) {
				action.execute(block);
			} else {
				throw new IllegalArgumentException(
						"An element is already present at key \"" + name + "\" with value " + value + "."
				);
			}
		}

		public boolean isEmpty() {
			return this.properties.isEmpty();
		}

		public void forEach(BiConsumer<String, CustomElement<?>> consumer) {
			this.properties.forEach(consumer);
		}

		public JsonObject toJson() {
			var json = new JsonObject();
			this.forEach((key, value) -> {
				json.add(key, value.toJson());
			});
			return json;
		}
	}
}
