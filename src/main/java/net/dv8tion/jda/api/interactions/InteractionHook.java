/*
 * Copyright 2015 Austin Keener, Michael Ritter, Florian Spieß, and the JDA contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dv8tion.jda.api.interactions;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.WebhookClient;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageUpdateAction;
import net.dv8tion.jda.api.utils.AttachmentOption;
import net.dv8tion.jda.internal.utils.Checks;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.io.File;
import java.io.InputStream;
import java.util.Collection;

/**
 * Webhook API for an interaction. Valid for up to 15 minutes after the interaction.
 * <br>This can be used to send followup messages or edit the original message of an interaction.
 *
 * <p>The interaction has to be acknowledged before any of these actions can be performed.
 * You need to call one of {@link Interaction#deferReply() deferReply()}, {@link Interaction#reply(String) reply(...)}, {@link ComponentInteraction#deferEdit() deferEdit()}, or {@link ComponentInteraction#editMessage(String) editMessage(...)} first.
 *
 * <p>When {@link Interaction#deferReply()} is used, the first message will act identically to {@link #editOriginal(String) editOriginal(...)}.
 * This means that you cannot make your deferred reply ephemeral through this interaction hook. You need to specify whether your reply is ephemeral or not directly in {@link Interaction#deferReply(boolean) deferReply(boolean)}.
 *
 * @see #editOriginal(String)
 * @see #deleteOriginal()
 * @see #sendMessage(String)
 */
public interface InteractionHook extends WebhookClient
{
    /**
     * The interaction attached to this hook.
     *
     * @return The {@link Interaction}
     */
    @Nonnull
    Interaction getInteraction();

    /**
     * Whether messages sent from this interaction hook should be ephemeral by default.
     * <br>This does not affect message updates, including deferred replies sent with {@link #sendMessage(String) sendMessage(...)} methods.
     *
     * @param  ephemeral
     *         True if messages should be ephemeral
     *
     * @return The same interaction hook instance
     */
    @Nonnull
    InteractionHook setEphemeral(boolean ephemeral);

    /**
     * The JDA instance for this interaction
     *
     * @return The JDA instance
     */
    @Nonnull
    JDA getJDA();

    /**
     * Retrieves the original reply to this interaction.
     * <br>This doesn't work for ephemeral messages and will always cause an unknown message error response.
     *
     * @return {@link RestAction} - Type: {@link Message}
     */
    @Nonnull
    @CheckReturnValue
    RestAction<Message> retrieveOriginal();

    /**
     * Edit the source message sent by this interaction.
     * <br>For {@link ComponentInteraction#editComponents(Collection)} and {@link ComponentInteraction#deferEdit()} this will be the message the components are attached to.
     * For {@link Interaction#deferReply()} and {@link Interaction#reply(String)} this will be the reply message instead.
     *
     * <p>This method will be delayed until the interaction is acknowledged.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_WEBHOOK UNKNOWN_WEBHOOK}
     *     <br>The webhook is no longer available, either it was deleted or in case of interactions it expired.</li>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MESSAGE UNKNOWN_MESSAGE}
     *     <br>The message for that id does not exist</li>
     * </ul>
     *
     * @param  content
     *         The new message content to use
     *
     * @throws IllegalArgumentException
     *         If the provided content is null, empty, or longer than {@link Message#MAX_CONTENT_LENGTH}
     *
     * @return {@link WebhookMessageUpdateAction}
     */
    @Nonnull
    @CheckReturnValue
    default WebhookMessageUpdateAction editOriginal(@Nonnull String content)
    {
        return editMessageById("@original", content);
    }

    /**
     * Edit the source message sent by this interaction.
     * <br>For {@link ComponentInteraction#editComponents(Collection)} and {@link ComponentInteraction#deferEdit()} this will be the message the components are attached to.
     * For {@link Interaction#deferReply()} and {@link Interaction#reply(String)} this will be the reply message instead.
     *
     * <p>This method will be delayed until the interaction is acknowledged.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_WEBHOOK UNKNOWN_WEBHOOK}
     *     <br>The webhook is no longer available, either it was deleted or in case of interactions it expired.</li>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MESSAGE UNKNOWN_MESSAGE}
     *     <br>The message for that id does not exist</li>
     * </ul>
     *
     * @param  components
     *         The new component layouts for this message, such as {@link net.dv8tion.jda.api.interactions.ActionRow ActionRows}
     *
     * @throws IllegalArgumentException
     *         If the provided components are null, or more than 5 layouts are provided
     *
     * @return {@link WebhookMessageUpdateAction}
     */
    @Nonnull
    @CheckReturnValue
    default WebhookMessageUpdateAction editOriginalComponents(@Nonnull Collection<? extends ComponentLayout> components)
    {
        return editMessageComponentsById("@original", components);
    }

    /**
     * Edit the source message sent by this interaction.
     * <br>For {@link ComponentInteraction#editComponents(Collection)} and {@link ComponentInteraction#deferEdit()} this will be the message the components are attached to.
     * For {@link Interaction#deferReply()} and {@link Interaction#reply(String)} this will be the reply message instead.
     *
     * <p>This method will be delayed until the interaction is acknowledged.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_WEBHOOK UNKNOWN_WEBHOOK}
     *     <br>The webhook is no longer available, either it was deleted or in case of interactions it expired.</li>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MESSAGE UNKNOWN_MESSAGE}
     *     <br>The message for that id does not exist</li>
     * </ul>
     *
     * @param  component
     *         The new component layout for this message, such as {@link net.dv8tion.jda.api.interactions.ActionRow ActionRows}
     * @param  components
     *         Additional component layouts to use
     *
     * @throws IllegalArgumentException
     *         If the provided components are null, or more than 5 layouts are provided
     *
     * @return {@link WebhookMessageUpdateAction}
     */
    @Nonnull
    @CheckReturnValue
    default WebhookMessageUpdateAction editOriginalComponents(@Nonnull ComponentLayout component, @Nonnull ComponentLayout... components)
    {
        return editMessageComponentsById("@original", component, components);
    }

    /**
     * Edit the source message sent by this interaction.
     * <br>For {@link ComponentInteraction#editComponents(Collection)} and {@link ComponentInteraction#deferEdit()} this will be the message the components are attached to.
     * For {@link Interaction#deferReply()} and {@link Interaction#reply(String)} this will be the reply message instead.
     *
     * <p>This method will be delayed until the interaction is acknowledged.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_WEBHOOK UNKNOWN_WEBHOOK}
     *     <br>The webhook is no longer available, either it was deleted or in case of interactions it expired.</li>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MESSAGE UNKNOWN_MESSAGE}
     *     <br>The message for that id does not exist</li>
     * </ul>
     *
     * @param  embeds
     *         {@link MessageEmbed MessageEmbeds} to use (up to 10 in total)
     *
     * @throws IllegalArgumentException
     *         If the provided embeds are null, or more than 10
     *
     * @return {@link WebhookMessageUpdateAction}
     */
    @Nonnull
    @CheckReturnValue
    default WebhookMessageUpdateAction editOriginalEmbeds(@Nonnull Collection<? extends MessageEmbed> embeds)
    {
        return editMessageEmbedsById("@original", embeds);
    }

    /**
     * Edit the source message sent by this interaction.
     * <br>For {@link ComponentInteraction#editComponents(Collection)} and {@link ComponentInteraction#deferEdit()} this will be the message the components are attached to.
     * For {@link Interaction#deferReply()} and {@link Interaction#reply(String)} this will be the reply message instead.
     *
     * <p>This method will be delayed until the interaction is acknowledged.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_WEBHOOK UNKNOWN_WEBHOOK}
     *     <br>The webhook is no longer available, either it was deleted or in case of interactions it expired.</li>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MESSAGE UNKNOWN_MESSAGE}
     *     <br>The message for that id does not exist</li>
     * </ul>
     *
     * @param  embed
     *         The new message embed to use
     * @param  embeds
     *         Additional {@link MessageEmbed MessageEmbeds} to use (up to 10 in total)
     *
     * @throws IllegalArgumentException
     *         If the provided embeds are null, or more than 10
     *
     * @return {@link WebhookMessageUpdateAction}
     */
    @Nonnull
    @CheckReturnValue
    default WebhookMessageUpdateAction editOriginalEmbeds(@Nonnull MessageEmbed embed, @Nonnull MessageEmbed... embeds)
    {
        return editMessageEmbedsById("@original", embed, embeds);
    }

    /**
     * Edit the source message sent by this interaction.
     * <br>For {@link ComponentInteraction#editComponents(Collection)} and {@link ComponentInteraction#deferEdit()} this will be the message the components are attached to.
     * For {@link Interaction#deferReply()} and {@link Interaction#reply(String)} this will be the reply message instead.
     *
     * <p>This method will be delayed until the interaction is acknowledged.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_WEBHOOK UNKNOWN_WEBHOOK}
     *     <br>The webhook is no longer available, either it was deleted or in case of interactions it expired.</li>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MESSAGE UNKNOWN_MESSAGE}
     *     <br>The message for that id does not exist</li>
     * </ul>
     *
     * @param  message
     *         The new message to replace the existing message with
     *
     * @throws IllegalArgumentException
     *         If the provided message is null
     *
     * @return {@link WebhookMessageUpdateAction}
     */
    @Nonnull
    @CheckReturnValue
    default WebhookMessageUpdateAction editOriginal(@Nonnull Message message)
    {
        return editMessageById("@original", message);
    }

    /**
     * Edit the source message sent by this interaction.
     * <br>For {@link ComponentInteraction#editComponents(Collection)} and {@link ComponentInteraction#deferEdit()} this will be the message the components are attached to.
     * For {@link Interaction#deferReply()} and {@link Interaction#reply(String)} this will be the reply message instead.
     *
     * <p>This method will be delayed until the interaction is acknowledged.
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_WEBHOOK UNKNOWN_WEBHOOK}
     *     <br>The webhook is no longer available, either it was deleted or in case of interactions it expired.</li>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MESSAGE UNKNOWN_MESSAGE}
     *     <br>The message for that id does not exist</li>
     * </ul>
     *
     * @param  format
     *         Format string for the message content
     * @param  args
     *         Format arguments for the content
     *
     * @throws IllegalArgumentException
     *         If the formatted string is null, empty, or longer than {@link Message#MAX_CONTENT_LENGTH}
     *
     * @return {@link WebhookMessageUpdateAction}
     */
    @Nonnull
    @CheckReturnValue
    default WebhookMessageUpdateAction editOriginalFormat(@Nonnull String format, @Nonnull Object... args)
    {
        Checks.notNull(format, "Format String");
        return editOriginal(String.format(format, args));
    }


    /**
     * Edit the source message sent by this interaction.
     * <br>For {@link ComponentInteraction#editComponents(Collection)} and {@link ComponentInteraction#deferEdit()} this will be the message the components are attached to.
     * For {@link Interaction#deferReply()} and {@link Interaction#reply(String)} this will be the reply message instead.
     * <br>The provided file will be appended to the message. You cannot delete or edit existing files on a message.
     *
     * <p>This method will be delayed until the interaction is acknowledged.
     *
     * <p><b>Uploading images with Embeds</b>
     * <br>When uploading an <u>image</u> you can reference said image using the specified filename as URI {@code attachment://filename.ext}.
     *
     * <p><u>Example</u>
     * <pre><code>
     * WebhookClient hook; // = reference of a WebhookClient such as interaction.getHook()
     * EmbedBuilder embed = new EmbedBuilder();
     * InputStream file = new FileInputStream("image.png"); // the name in your file system can be different from the name used in discord
     * embed.setImage("attachment://cat.png") // we specify this in sendFile as "cat.png"
     *      .setDescription("This is a cute cat :3");
     * hook.editOriginal(file, "cat.png").setEmbeds(embed.build()).queue();
     * </code></pre>
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_WEBHOOK UNKNOWN_WEBHOOK}
     *     <br>The webhook is no longer available, either it was deleted or in case of interactions it expired.</li>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MESSAGE UNKNOWN_MESSAGE}
     *     <br>The message for that id does not exist</li>
     * </ul>
     *
     * @param  data
     *         The InputStream data to upload to the webhook.
     * @param  name
     *         The file name that should be sent to discord
     *         <br>Refer to the documentation for {@link #sendFile(java.io.File, String, AttachmentOption...)} for information about this parameter.
     * @param  options
     *         Possible options to apply to this attachment, such as marking it as spoiler image
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided data, or filename is {@code null}.
     *
     * @return {@link WebhookMessageUpdateAction}
     */
    @Nonnull
    @CheckReturnValue
    default WebhookMessageUpdateAction editOriginal(@Nonnull InputStream data, @Nonnull String name, @Nonnull AttachmentOption... options)
    {
        return editMessageById("@original", data, name, options);
    }

    /**
     * Edit the source message sent by this interaction.
     * <br>For {@link ComponentInteraction#editComponents(Collection)} and {@link ComponentInteraction#deferEdit()} this will be the message the components are attached to.
     * For {@link Interaction#deferReply()} and {@link Interaction#reply(String)} this will be the reply message instead.
     * <br>The provided file will be appended to the message. You cannot delete or edit existing files on a message.
     *
     * <p>This method will be delayed until the interaction is acknowledged.
     *
     * <p>This is a shortcut to {@link #editOriginal(java.io.File, String, AttachmentOption...)} by way of using {@link java.io.File#getName()}.
     * <pre>editOriginal(file, file.getName())</pre>
     *
     * <p><b>Uploading images with Embeds</b>
     * <br>When uploading an <u>image</u> you can reference said image using the specified filename as URI {@code attachment://filename.ext}.
     *
     * <p><u>Example</u>
     * <pre><code>
     * WebhookClient hook; // = reference of a WebhookClient such as interaction.getHook()
     * EmbedBuilder embed = new EmbedBuilder();
     * File file = new File("image.png"); // the name in your file system can be different from the name used in discord
     * embed.setImage("attachment://cat.png") // we specify this in sendFile as "cat.png"
     *      .setDescription("This is a cute cat :3");
     * hook.editOriginal(file, "cat.png").setEmbeds(embed.build()).queue();
     * </code></pre>
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_WEBHOOK UNKNOWN_WEBHOOK}
     *     <br>The webhook is no longer available, either it was deleted or in case of interactions it expired.</li>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MESSAGE UNKNOWN_MESSAGE}
     *     <br>The message for that id does not exist</li>
     * </ul>
     *
     * @param  file
     *         The {@link File} data to upload to the webhook.
     * @param  options
     *         Possible options to apply to this attachment, such as marking it as spoiler image
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided file is {@code null}.
     *
     * @return {@link WebhookMessageUpdateAction}
     */
    @Nonnull
    @CheckReturnValue
    default WebhookMessageUpdateAction editOriginal(@Nonnull File file, @Nonnull AttachmentOption... options)
    {
        return editMessageById("@original", file, options);
    }

    /**
     * Edit the source message sent by this interaction.
     * <br>For {@link ComponentInteraction#editComponents(Collection)} and {@link ComponentInteraction#deferEdit()} this will be the message the components are attached to.
     * For {@link Interaction#deferReply()} and {@link Interaction#reply(String)} this will be the reply message instead.
     * <br>The provided file will be appended to the message. You cannot delete or edit existing files on a message.
     *
     * <p>This method will be delayed until the interaction is acknowledged.
     *
     * <p><b>Uploading images with Embeds</b>
     * <br>When uploading an <u>image</u> you can reference said image using the specified filename as URI {@code attachment://filename.ext}.
     *
     * <p><u>Example</u>
     * <pre><code>
     * WebhookClient hook; // = reference of a WebhookClient such as interaction.getHook()
     * EmbedBuilder embed = new EmbedBuilder();
     * File file = new File("image.png"); // the name in your file system can be different from the name used in discord
     * embed.setImage("attachment://cat.png") // we specify this in sendFile as "cat.png"
     *      .setDescription("This is a cute cat :3");
     * hook.editOriginal(file, "cat.png").setEmbeds(embed.build()).queue();
     * </code></pre>
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_WEBHOOK UNKNOWN_WEBHOOK}
     *     <br>The webhook is no longer available, either it was deleted or in case of interactions it expired.</li>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MESSAGE UNKNOWN_MESSAGE}
     *     <br>The message for that id does not exist</li>
     * </ul>
     *
     * @param  file
     *         The {@link File} data to upload to the webhook.
     * @param  name
     *         The file name that should be sent to discord
     *         <br>Refer to the documentation for {@link #sendFile(java.io.File, String, AttachmentOption...)} for information about this parameter.
     * @param  options
     *         Possible options to apply to this attachment, such as marking it as spoiler image
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided file or filename is {@code null}.
     *
     * @return {@link WebhookMessageUpdateAction}
     */
    @Nonnull
    @CheckReturnValue
    default WebhookMessageUpdateAction editOriginal(@Nonnull File file, @Nonnull String name, @Nonnull AttachmentOption... options)
    {
        return editMessageById("@original", file, name, options);
    }

    /**
     * Edit the source message sent by this interaction.
     * <br>For {@link ComponentInteraction#editComponents(Collection)} and {@link ComponentInteraction#deferEdit()} this will be the message the components are attached to.
     * For {@link Interaction#deferReply()} and {@link Interaction#reply(String)} this will be the reply message instead.
     * <br>The provided file will be appended to the message. You cannot delete or edit existing files on a message.
     *
     * <p>This method will be delayed until the interaction is acknowledged.
     *
     * <p><b>Uploading images with Embeds</b>
     * <br>When uploading an <u>image</u> you can reference said image using the specified filename as URI {@code attachment://filename.ext}.
     *
     * <p><u>Example</u>
     * <pre><code>
     * WebhookClient hook; // = reference of a WebhookClient such as interaction.getHook()
     * EmbedBuilder embed = new EmbedBuilder();
     * InputStream file = new FileInputStream("image.png"); // the name in your file system can be different from the name used in discord
     * embed.setImage("attachment://cat.png") // we specify this in sendFile as "cat.png"
     *      .setDescription("This is a cute cat :3");
     * hook.editOriginal(file, "cat.png").setEmbeds(embed.build()).queue();
     * </code></pre>
     *
     * <p>Possible {@link net.dv8tion.jda.api.requests.ErrorResponse ErrorResponses} include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_WEBHOOK UNKNOWN_WEBHOOK}
     *     <br>The webhook is no longer available, either it was deleted or in case of interactions it expired.</li>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MESSAGE UNKNOWN_MESSAGE}
     *     <br>The message for that id does not exist</li>
     * </ul>
     *
     * @param  data
     *         The InputStream data to upload to the webhook.
     * @param  name
     *         The file name that should be sent to discord
     *         <br>Refer to the documentation for {@link #sendFile(java.io.File, String, AttachmentOption...)} for information about this parameter.
     * @param  options
     *         Possible options to apply to this attachment, such as marking it as spoiler image
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided data or filename is {@code null}.
     *
     * @return {@link WebhookMessageUpdateAction}
     */
    @Nonnull
    @CheckReturnValue
    default WebhookMessageUpdateAction editOriginal(@Nonnull byte[] data, @Nonnull String name, @Nonnull AttachmentOption... options)
    {
        return editMessageById("@original", data, name, options);
    }

    /**
     * Delete the original reply.
     * <br>This doesn't work for ephemeral messages.
     *
     * @return {@link RestAction}
     */
    @Nonnull
    @CheckReturnValue
    default RestAction<Void> deleteOriginal()
    {
        return deleteMessageById("@original");
    }
}
