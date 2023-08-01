<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Reference extends Model
{
    public $timestamps = false;
    
    protected $table = 'reference'; // Replace 'your_table_name' with the actual name of your database table
    protected $fillable = ['response'];
    // Define the fillable properties (columns) that can be mass-assigned
}