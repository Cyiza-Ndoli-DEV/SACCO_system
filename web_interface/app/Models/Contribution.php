<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Contribution extends Model
{
    use HasFactory;
    use HasFactory;
    public $timestamps = false;
    
    protected $table = 'contributions'; // Replace 'your_table_name' with the actual name of your database table
    protected $fillable = ['memberNumber', 'amount', 'date'];
}
