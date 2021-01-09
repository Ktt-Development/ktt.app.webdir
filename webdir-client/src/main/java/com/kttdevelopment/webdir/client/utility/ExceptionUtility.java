/*
 * Copyright (C) 2021 Ktt Development
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.kttdevelopment.webdir.client.utility;

import com.kttdevelopment.webdir.client.utility.exceptions.ExceptionRunnable;
import com.kttdevelopment.webdir.client.utility.exceptions.ExceptionSupplier;

import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class ExceptionUtility {

    public static String getStackTraceAsString(final Throwable throwable){
        final StringWriter err = new StringWriter();
        throwable.printStackTrace(new PrintWriter(err));
        return err.toString();
    }

    public static void runIgnoreException(final ExceptionRunnable runnable){
        try{ runnable.run();
        }catch(final Throwable ignored){ }
    }

    public static <T> T requireNonExceptionElse(final ExceptionSupplier<T> supplier, final T defaultObj){
        try{ return supplier.get();
        }catch(final Throwable ignored){ return defaultObj; }
    }

    @SuppressWarnings({"unchecked", "UnusedReturnValue"})
    public static <T extends Throwable> RuntimeException throwUnchecked(final Throwable throwable) throws T {
        throw (T) throwable;
    }

}
