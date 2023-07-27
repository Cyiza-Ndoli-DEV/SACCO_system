<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('available_deposits', function (Blueprint $table) {
            $table->increments('deposit_id');
            $table->integer('member_number');
            $table->integer('amount_deposited');
            $table->date('deposit_date');
            $table->integer('receipt_number');
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('available_deposits');
    }
};
